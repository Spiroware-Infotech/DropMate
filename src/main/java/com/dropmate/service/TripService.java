package com.dropmate.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dropmate.dto.PlaceInfo;
import com.dropmate.dto.TripRequest;
import com.dropmate.dto.TripResponse;
import com.dropmate.entity.DriverProfile;
import com.dropmate.entity.Trip;
import com.dropmate.entity.TripStop;
import com.dropmate.entity.User;
import com.dropmate.enums.TripStatus;
import com.dropmate.enums.TripType;
import com.dropmate.repository.DriverProfileRepository;
import com.dropmate.repository.TripRepository;
import com.dropmate.repository.TripStopRepository;
import com.dropmate.repository.UserRepository;
import com.dropmate.utils.GeoUtils;
import com.dropmate.utils.LocationUtils;
import com.dropmate.utils.RouteUtils;
import com.dropmate.utils.TimeUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TripService {

	private static double radiusKm= 150;
	
	private final TripRepository tripRepository;
	private final UserRepository userRepository;
	private final TripStopRepository tripStopRepository;
	private final DriverProfileRepository driverProfileRepository;
	private final GeocodingService geocodingService;
	 
	public Trip createTrip(Long driverId, Trip trip, List<TripStop> stops) {
		User user = userRepository.findById(driverId).orElseThrow(() -> new RuntimeException("Driver not found"));

		DriverProfile driver = driverProfileRepository.findUserById(user.getUserId());
		
		trip.setId(UUID.randomUUID().toString());
		trip.setDriver(driver);
		trip.setCreatedAt(LocalDateTime.now());
		trip.setUpdatedAt(LocalDateTime.now());
		Trip savedTrip = tripRepository.save(trip);

		if (stops != null) {
			for (TripStop stop : stops) {
				stop.setTrip(savedTrip);
				tripStopRepository.save(stop);
			}
		}
		return savedTrip;
	}

	public void cancelTrip(String tripId) {
		Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Trip not found"));
		trip.setStatus(TripStatus.CANCELLED);
		tripRepository.save(trip);
	}

	public List<Trip> searchTrips(String originWkt, double radius, Timestamp from, Timestamp to) {
		return tripRepository.findUpcomingTripsNear(from, to, originWkt, radius);
	}

	public List<Trip> searchRides(double userSrcLat, double userSrcLng, double userDestLat, double userDestLng,
			LocalDate date) {

		List<Trip> trips = tripRepository.findByStartDate(date);

		return trips.stream().filter(trip -> {
			double srcLat = LocationUtils.getLatitude(trip.getSourceJson());
			double srcLng = LocationUtils.getLongitude(trip.getSourceJson());
			double destLat = LocationUtils.getLatitude(trip.getDestinationJson());
			double destLng = LocationUtils.getLongitude(trip.getDestinationJson());

			double sourceDistance = GeoUtils.distanceKm(userSrcLat, userSrcLng, srcLat, srcLng);
			double destinationDistance = GeoUtils.distanceKm(userDestLat, userDestLng, destLat, destLng);

			// both source and destination should be near
			return sourceDistance <= radiusKm && destinationDistance <= radiusKm;
		}).collect(Collectors.toList());
	}
	
	
	public List<TripResponse> searchRides(String source, String destination, LocalDate date, Integer seats) {
		log.info("Search rides based on selected Date: {}", date);
		try {
			
            double[] srcCoords =  geocodingService.getCoordinates(source);
            double[] destCoords =  geocodingService.getCoordinates(destination);
            log.info("Soruce Coords: {}", srcCoords);
            log.info("Destination Coords: {}", destCoords);
            
            double userSrcLat = srcCoords[0];
            double userSrcLng = srcCoords[1]; 
            double userDestLat = destCoords[0]; 
            double userDestLng = destCoords[1];
            
            List<Trip> trips = tripRepository.findByStartDate(date);
            log.info("rides size in DB : {}", trips.size());
            
            List<TripResponse> rides = trips.stream().map(this::mapToResponse).collect(Collectors.toList());
    		return rides.stream().filter(trip -> {
    			PlaceInfo src = LocationUtils.getPlaceInfo(trip.getSourceJson());
    			PlaceInfo dest = LocationUtils.getPlaceInfo(trip.getDestinationJson());
    			trip.setSource(src);
    			trip.setDestination(dest);
    			
    			double sourceDistance = GeoUtils.distanceKm(userSrcLat, userSrcLng, src.getLatitude(), src.getLongitude());
    			double destinationDistance = GeoUtils.distanceKm(userDestLat, userDestLng, dest.getLatitude(), dest.getLongitude());

    			// both source and destination should be near
    			return sourceDistance <= radiusKm && destinationDistance <= radiusKm;
    			
    		}).collect(Collectors.toList());
    		
        } catch (Exception e) {
           log.error("Error : {}", e.getMessage(), e);
        }
		return null;
	}
	
	
	public List<TripResponse> searchTripsBySourceAndDestination(
	        String source,
	        String destination,
	        LocalDate date,
	        Integer seats) {

	    log.info("Searching trips date: {}, seats: {}", date, seats);

	    try {
	        // 1) fetch trips for date
	        List<Trip> allTrips = tripRepository.findByStartDate(date);
	        log.info("All rides found for Date: {}, Rides: {}", date, allTrips.size());

	        List<TripResponse> rides = allTrips.stream()
	                .map(this::mapToResponse)
	                .collect(Collectors.toList());

	        // 2) geocode search source and destination (we need both coords to measure distance)
	        double[] srcCoords = geocodingService.getCoordinates(source);
	        double[] destCoords = geocodingService.getCoordinates(destination);
	        double userSrcLat = srcCoords[0];
	        double userSrcLng = srcCoords[1];
	        double userDestLat = destCoords[0];
	        double userDestLng = destCoords[1];

	        // 3) filter
	        List<TripResponse> finalFilterRides = rides.stream()
	                .filter(trip -> {
	                    PlaceInfo tripSource = LocationUtils.getPlaceInfo(trip.getSourceJson());
	                    PlaceInfo tripDestination = LocationUtils.getPlaceInfo(trip.getDestinationJson());

	                    // Defensive null checks
	                    if (tripSource == null || tripDestination == null) {
	                        return false;
	                    }

	                    // SOURCE: allow exact name OR within radius of user's source
	                    boolean sourceMatchesByName = tripSource.getAddress() != null &&
	                            (tripSource.getAddress().equalsIgnoreCase(source) || tripSource.getName().equalsIgnoreCase(source));

	                    boolean sourceMatchesByRadius = false;
	                    if (tripSource.getLatitude() != 0 && tripSource.getLongitude() != 0) {
	                        sourceMatchesByRadius = GeoUtils.distanceKm(
	                                tripSource.getLatitude(), tripSource.getLongitude(),
	                                userSrcLat, userSrcLng) <= radiusKm;
	                    }

	                    boolean sourceMatches = sourceMatchesByName || sourceMatchesByRadius;

	                    // DESTINATION: strict name match (you said destination must match by name)
	                    boolean destinationMatches = tripDestination.getName() != null &&
	                            (tripDestination.getName().equalsIgnoreCase(destination) || tripDestination.getAddress().equalsIgnoreCase(destination));

	                    // SEATS: availableSeats >= requested seats
	                    boolean seatsAvailable = true; // default true if seats param is null
	                    if (seats != null) {
	                        Integer available = trip.getAvailableSeats();
	                        seatsAvailable = available != null && available >= seats;
	                    }

	                    return sourceMatches && destinationMatches && seatsAvailable;
	                })
	                .collect(Collectors.toList());

	        log.info("Filtered rides count: {}", finalFilterRides.size());
	        return finalFilterRides;

	    } catch (Exception e) {
	        log.error("Error searching trips: {}", e.getMessage(), e);
	        return Collections.emptyList();
	    }
	}



	 
	public Trip getTripById(String tripId) {
		return tripRepository.findById(tripId).orElse(null);
	}

	public void updateTrip(Long tripId, TripRequest tripRequest) {
		// TODO Auto-generated method stub

	}

	public void deleteTrip(Long tripId) {
		// TODO Auto-generated method stub

	}

	public List<TripResponse> searchTrips(String from, String to, String date) {
		// TODO Auto-generated method stub
		return null;
	}

	public void createTrip(TripRequest tripRequest) {
		// TODO Auto-generated method stub

	}

	public List<TripResponse> getAllTrips() {
		List<Trip> trips = tripRepository.findByTripType(TripType.PASSENGER);
		log.info("All trips size: {} ",trips.size());
		return trips.stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	
	public Map<String, List<Trip>> getTripsByDateCategory() {
		List<Trip> trips = tripRepository.findByTripType(TripType.PASSENGER);

		LocalDate today = LocalDate.now();
		LocalDate tomorrow = today.plusDays(1);

		Map<String, List<Trip>> grouped = new LinkedHashMap<>();

		List<Trip> todayTrips = trips.stream().filter(t -> t.getStartDate().isEqual(today))
				.collect(Collectors.toList());

		List<Trip> tomorrowTrips = trips.stream().filter(t -> t.getStartDate().isEqual(tomorrow))
				.collect(Collectors.toList());

		List<Trip> upcomingTrips = trips.stream().filter(t -> t.getStartDate().isAfter(tomorrow))
				.collect(Collectors.toList());

		List<Trip> olderTrips = trips.stream().filter(t -> t.getStartDate().isBefore(today))
				.collect(Collectors.toList());

		if (!todayTrips.isEmpty())
			grouped.put("Today", todayTrips);
		if (!tomorrowTrips.isEmpty())
			grouped.put("Tomorrow", tomorrowTrips);
		if (!upcomingTrips.isEmpty())
			grouped.put("Upcoming", upcomingTrips);
		if (!olderTrips.isEmpty())
			grouped.put("Older", olderTrips);

		return grouped;
	}

	public List<TripResponse> getTripsByStatus(String status) {
		List<Trip> trips = tripRepository.findByTripTypeAndStatus(TripType.PASSENGER, status);
		log.info("trips size: {} ",trips.size());
		return trips.stream().map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	private TripResponse mapToResponse(Trip trip) {
		TripResponse response = new TripResponse();

		long durationSeconds = trip.getDuration();
		LocalTime startTime = trip.getStartTime(); // if stored as LocalTime

		response.setId(trip.getId());
		response.setTripDate(trip.getStartDate());
		response.setStartTime(startTime.format(DateTimeFormatter.ofPattern("HH:mm")));
		response.setEndTime(TimeUtils.calculateEndTime(response.getStartTime(), durationSeconds));
		response.setStartLocation(trip.getOriginName());
		response.setEndLocation(trip.getDestinationName());
		response.setStatus(trip.getStatus().name());
		response.setDuration(RouteUtils.formatHoursAndMinutes(durationSeconds, false));
		response.setBookingType(trip.getBookingType());
		response.setDistance(RouteUtils.formatDistance(trip.getDistance()));
		response.setSourceJson(trip.getSourceJson());
		response.setDestinationJson(trip.getDestinationJson());
		response.setPricePerSeat(trip.getPricePerSeat());
		response.setDriverName(trip.getDriver().getUser().getFirstname());
		response.setAvailableSeats(trip.getAvailableSeats());
		return response;
	}

	public Map<String, Object> groupTripsByDate(List<TripResponse> trips) {
	    LocalDate today = LocalDate.now();
	    LocalDate tomorrow = today.plusDays(1);

	    Map<String, Object> grouped = new LinkedHashMap<>();
	    List<TripResponse> todayList = new ArrayList<>();
	    List<TripResponse> tomorrowList = new ArrayList<>();
	    Map<LocalDate, List<TripResponse>> upcomingMap = new TreeMap<>(); // sorted by date ascending
	    List<TripResponse> olderList = new ArrayList<>();

	    for (TripResponse trip : trips) {
	        LocalDate date = trip.getTripDate();

	        if (date.isEqual(today)) {
	            todayList.add(trip);
	        } else if (date.isEqual(tomorrow)) {
	            tomorrowList.add(trip);
	        } else if (date.isBefore(today)) {
	            olderList.add(trip);
	        } else {
	            // Group future trips by each date, sorted automatically
	            upcomingMap.computeIfAbsent(date, k -> new ArrayList<>()).add(trip);
	        }
	    }

	    if (!todayList.isEmpty()) grouped.put("Today", todayList);
	    if (!tomorrowList.isEmpty()) grouped.put("Tomorrow", tomorrowList);
	    if (!upcomingMap.isEmpty()) grouped.put("Upcoming", upcomingMap);
	    if (!olderList.isEmpty()) grouped.put("Older", olderList);

	    return grouped;
	}

	public List<TripResponse> findByDriverId(Long userId) {
		List<Trip> trips = tripRepository.findByDriverId(userId);
		log.info("findByDriverId size: {} ",trips.size());
		return trips.stream().map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	

}
