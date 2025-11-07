package com.dropmate.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dropmate.dto.MyRidesResponse;
import com.dropmate.dto.PlaceInfo;
import com.dropmate.dto.RideRequest;
import com.dropmate.dto.TripResponse;
import com.dropmate.entity.DriverProfile;
import com.dropmate.entity.Rides;
import com.dropmate.entity.TripStop;
import com.dropmate.entity.User;
import com.dropmate.enums.KycStatus;
import com.dropmate.enums.TripStatus;
import com.dropmate.enums.TripType;
import com.dropmate.enums.VehicleType;
import com.dropmate.generators.GlobalIdGeneratorULID;
import com.dropmate.repository.DriverProfileRepository;
import com.dropmate.repository.RidesRepository;
import com.dropmate.repository.TripStopRepository;
import com.dropmate.repository.UserRepository;
import com.dropmate.utils.CommonUtil;
import com.dropmate.utils.RouteUtils;
import com.dropmate.utils.TimeUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RidesServices {

	private static double radiusKm= 150;
	
	private final UserRepository userRepository;
	private final TripStopRepository tripStopRepository;
	private final DriverProfileRepository profileRepository;
	private final GeocodingService geocodingService;
	private final RidesRepository ridesRespository;
	
	
	public Rides createRide(RideRequest request, User user) {
		Optional<DriverProfile> optionalProfile = profileRepository.findById(user.getUserId());

		DriverProfile driver = optionalProfile.orElseGet(() -> {
		    // Create and return a new DriverProfile if it doesn't exist
		    DriverProfile newProfile = DriverProfile.builder()
		    		.id(user.getUserId())
		    		.firstname(user.getFirstname())
		            .kycStatus(KycStatus.PENDING)
		            .vehicleType(VehicleType.CAR)
		            .build();
		    return profileRepository.saveAndFlush(newProfile); // must RETURN the saved object
		});
		
		TripStatus rideStatus= null;
		if(request.getReturnRideOption()!=null) {
			if(request.getReturnRideOption().equalsIgnoreCase("yes"))
				rideStatus = TripStatus.SCHEDULED;
			else if(request.getReturnRideOption().equalsIgnoreCase("later"))
				rideStatus = TripStatus.PENDING;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate startDate = LocalDate.parse(request.getDepartureDate(), formatter);
		LocalTime time = LocalTime.parse(request.getDepartureTime(), DateTimeFormatter.ofPattern("HH:mm"));
		
		PlaceInfo sourceJson = CommonUtil.fromJsonString(request.getSourceJson(), PlaceInfo.class);
		PlaceInfo destinationJson = CommonUtil.fromJsonString(request.getDestinationJson(), PlaceInfo.class);
		
		 // Save to DB here
		Rides ride = Rides.builder()
						.driver(user)
						.source(request.getDeparture())
						.destination(request.getArrival())
						.sourceJson(CommonUtil.convertToJsonString(sourceJson))
						.destinationJson(CommonUtil.convertToJsonString(destinationJson))
						.availableSeats(request.getSeats())
						.totalSeats(request.getSeats())
						.tripType(TripType.PASSENGER)
						.status(rideStatus)
						.startDate(startDate)
						.startTime(time)
						.price(request.getPrice())
						//.vehicleType(VehicleType.valueOf(request.getVehicleType()))
						.vehicleType(VehicleType.CAR)
						.bookingType(request.getBookingType().toUpperCase())
						.durationInSec(request.getDuration())
						.distanceKm(request.getDistance())
						.comment(request.getComment())
						.isActive(true)
						.build();
			
			ride.setId(GlobalIdGeneratorULID.generateRideId());
			ride.setCreatedAt(LocalDateTime.now());
			ride.setUpdatedAt(LocalDateTime.now());
			Rides savedTrip = ridesRespository.save(ride);
			
			List<TripStop> stops = request.getStops();
			if (stops != null) {
				for (TripStop stop : stops) {
					stop.setRides(savedTrip);
					tripStopRepository.save(stop);
				}
			}
			
		return savedTrip;
	}
	

	 
	public Rides getRideById(String tripId) {
		return ridesRespository.findById(tripId).orElse(null);
	}
	

	public List<TripResponse> getAllTrips(Long userId) {
		List<Rides> rides = ridesRespository.findAllMyRides(userId);
		log.info("All rides size: {} ",rides.size());
		// Merge both lists and remove duplicates (if any)
		return rides.stream().map(this::mapToResponse).collect(Collectors.toList());
	}
	
	public MyRidesResponse getMyRides(Long userId) {
	    List<Rides> created = ridesRespository.findRidesCreatedByUser(userId);
	    List<Rides> booked = ridesRespository.findRidesBookedByUser(userId);
	    log.info("Created rides size: {} ",created.size());
	    log.info("Booked rides size: {} ",booked.size());
	    return new MyRidesResponse(created, booked);
	}
	
	public List<TripResponse> getTripsByStatus(String status, Long userId) {
		List<Rides> trips = ridesRespository.findMyPassengerRides(TripType.PASSENGER, status, userId);
		log.info("Rides size: {} ",trips.size());
		return trips.stream().map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	private TripResponse mapToResponse(Rides ride) {
		TripResponse response = new TripResponse();

		long durationSeconds = ride.getDurationInSec();
		LocalTime startTime = ride.getStartTime(); // if stored as LocalTime

		response.setId(ride.getId());
		response.setTripDate(ride.getStartDate());
		response.setStartTime(startTime.format(DateTimeFormatter.ofPattern("HH:mm")));
		response.setEndTime(TimeUtils.calculateEndTime(response.getStartTime(), durationSeconds));
		response.setStartLocation(ride.getSource());
		response.setEndLocation(ride.getDestination());
		response.setStatus(ride.getStatus().name());
		response.setDuration(RouteUtils.formatHoursAndMinutes(durationSeconds, false));
		response.setBookingType(ride.getBookingType());
		response.setDistance(RouteUtils.formatDistance(ride.getDistanceKm()));
		response.setSourceJson(ride.getSourceJson());
		response.setDestinationJson(ride.getDestinationJson());
		response.setPricePerSeat(ride.getPrice());
		response.setDriverName(ride.getDriver().getFirstname());
		response.setAvailableSeats(ride.getAvailableSeats());
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
		List<Rides> trips = ridesRespository.findAllMyRides(userId);
		log.info("findByDriverId size: {} ",trips.size());
		return trips.stream().map(this::mapToResponse)
				.collect(Collectors.toList());
	}



	public List<TripResponse> findRidesBookedByUser(Long userId) {
		List<Rides> rides = ridesRespository.findRidesBookedByUser(userId);
		log.info("Booked Rides size: {} ",rides.size());
		return rides.stream().map(this::mapToResponse)
				.collect(Collectors.toList());
	}
}
