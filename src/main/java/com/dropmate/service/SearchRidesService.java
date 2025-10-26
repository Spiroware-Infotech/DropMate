package com.dropmate.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dropmate.dto.PlaceInfo;
import com.dropmate.dto.TripSearchRequest;
import com.dropmate.entity.Rides;
import com.dropmate.repository.DriverProfileRepository;
import com.dropmate.repository.RidesRespository;
import com.dropmate.repository.TripStopRepository;
import com.dropmate.repository.UserRepository;
import com.dropmate.search.RidesResponse;
import com.dropmate.utils.GeoUtils;
import com.dropmate.utils.LocationUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SearchRidesService {

	private final RidesRespository ridesRespository;
	private final UserRepository userRepository;
	private final TripStopRepository tripStopRepository;
	private final DriverProfileRepository driverProfileRepository;
	private final com.dropmate.search.GeocodingService geocodingService;
	


    private static final double INITIAL_RADIUS_KM = 50.0;
    private static final double MAX_RADIUS_KM = 100.0;
    private static final double RADIUS_INCREMENT = 50.0;
    private static final double BEARING_TOLERANCE = 35.0; // degrees

    public List<RidesResponse> searchTripsBySourceAndDestination(TripSearchRequest req) {
        log.info("Searching Trips: src={}, dest={}, date={}, seats={}", req.getSource(), req.getDestination(), req.getDate(), req.getRequiredSeats());

        String source = req.getSource();
        String destination= req.getDestination();
        String sourcePlaceId= req.getSourcePlaceId();
        String destPlaceId= req.getDestPlaceId();
        LocalDate date= req.getDate();
        Integer requiredSeats = req.getRequiredSeats();
        
        try {
            // ✅ Step 1: Fetch coordinates from placeId (fallback to geocoding by name)
            double[] srcCoords = getCoordinates(sourcePlaceId, source);
            double[] destCoords = getCoordinates(destPlaceId, destination);

            // ✅ Step 2: Fetch trips for same date
            List<Rides> allTrips = ridesRespository.findByStartDate(date);
            log.info("Total Trips Found for {}: , size: {}", date, allTrips.size());

            // ✅ Step 3: Apply radius filter (expand dynamically)
            double radius = INITIAL_RADIUS_KM;
            List<RidesResponse> matchedRides = new ArrayList<>();

            while (radius <= MAX_RADIUS_KM && matchedRides.isEmpty()) {
                double currentRadius = radius;
                log.info("Filtering trips within radius {} km...", currentRadius);

                matchedRides = allTrips.stream()
                        .filter(trip -> trip.getAvailableSeats() >= requiredSeats)
                        .filter(trip -> trip.getIsActive()) // Optional: only active rides
                        .filter(trip -> isTripMatching(trip, srcCoords, destCoords, currentRadius))
                        .map(trip -> mapToResponse(trip, srcCoords[0], srcCoords[1])) // pass user coordinates
                        .sorted(Comparator.comparingDouble(tr -> tr.getDistanceFromUser())) // sort by closeness
                        .collect(Collectors.toList());

                radius += RADIUS_INCREMENT;
            }

            log.info("Final matched trips found: {}", matchedRides.size());
            return matchedRides;

        } catch (Exception e) {
            log.error("Error while searching trips: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // ✅ Step 4: Helper — fetch coordinates from placeId or fallback to name
    private double[] getCoordinates(String placeId, String fallbackName) {
        try {
            if (placeId != null && !placeId.isBlank()) {
                return geocodingService.getCoordinatesByPlaceId(placeId);
            } else {
                return geocodingService.getCoordinates(fallbackName);
            }
        } catch (Exception e) {
            log.warn("Geocoding failed for placeId={}, fallbackName={}", placeId, fallbackName);
            return new double[]{0.0, 0.0};
        }
    }

    // ✅ Step 5: Core logic for source + destination match + direction
    private boolean isTripMatching(Rides trip, double[] userSrc, double[] userDest, double radiusKm) {
        PlaceInfo tripSrc = LocationUtils.getPlaceInfo(trip.getSourceJson());
        PlaceInfo tripDest = LocationUtils.getPlaceInfo(trip.getDestinationJson());

        if (tripSrc == null || tripDest == null) return false;

        // 🟢 Match if trip source is near user source
        boolean sourceMatch = GeoUtils.distanceKm(
                tripSrc.getLatitude(), tripSrc.getLongitude(),
                userSrc[0], userSrc[1]) <= radiusKm;

        // 🟢 Match if trip destination is near user destination
        boolean destMatch = GeoUtils.distanceKm(
                tripDest.getLatitude(), tripDest.getLongitude(),
                userDest[0], userDest[1]) <= radiusKm;

        // 🟢 Optional Direction Check — ensure same travel direction
        boolean directionMatch = isSameDirection(
                tripSrc.getLatitude(), tripSrc.getLongitude(),
                tripDest.getLatitude(), tripDest.getLongitude(),
                userSrc[0], userSrc[1],
                userDest[0], userDest[1]);

        return sourceMatch && destMatch && directionMatch;
    }

    // ✅ Step 6: Check if both are heading in similar direction (using bearing)
    private boolean isSameDirection(double tripSrcLat, double tripSrcLng,
                                    double tripDestLat, double tripDestLng,
                                    double userSrcLat, double userSrcLng,
                                    double userDestLat, double userDestLng) {

        double tripBearing = GeoUtils.calculateBearing(tripSrcLat, tripSrcLng, tripDestLat, tripDestLng);
        double userBearing = GeoUtils.calculateBearing(userSrcLat, userSrcLng, userDestLat, userDestLng);

        return Math.abs(tripBearing - userBearing) <= BEARING_TOLERANCE;
    }

    // ✅ Step 7: Map entity → response object
    private RidesResponse mapToResponse(Rides trip,double userLat, double userLng) {
        if (trip == null) return null;

        // Safe copy using LocationUtils
        PlaceInfo sourceInfo = LocationUtils.getPlaceInfo(trip.getSource());
        PlaceInfo destInfo = LocationUtils.getPlaceInfo(trip.getDestination());

        // Calculate distance from user source if needed
        double distanceFromUser = LocationUtils.distanceKm(sourceInfo,
                PlaceInfo.builder().latitude(userLat).longitude(userLng).build());

        return RidesResponse.builder()
                .rideId(trip.getId())
                .driverId(trip.getDriver().getId())
                .driverName(trip.getDriver().getUser().getFirstname())
                .source(trip.getSource())
                .destination(trip.getDestination())
                .sourceJson(sourceInfo)
                .destinationJson(destInfo)
                .startDate(trip.getStartDate())
                .startTime(trip.getStartTime())
                .availableSeats(trip.getAvailableSeats())
                .vehicleType(trip.getVehicleType())
                .price(trip.getPrice())
                .status(trip.getStatus())
                .isActive(trip.getIsActive())
                .distanceFromUser(distanceFromUser)
                .totalDistanceKm(trip.getDistanceKm())
                .build();
    }
}
