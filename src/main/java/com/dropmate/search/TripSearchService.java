package com.dropmate.search;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dropmate.dto.PlaceInfo;
import com.dropmate.entity.Trip;
import com.dropmate.repository.TripRepository;
import com.dropmate.utils.LocationUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TripSearchService {

    private final TripRepository tripRepository;


//    public List<Trip> findBestMatchRides(String sourcePlaceId, String destinationPlaceId,
//                                         double radiusKm, LocalDate date) {
//
//        PlaceInfo userSource = LocationUtils.getPlaceInfo(sourcePlaceId);
//        PlaceInfo userDest = LocationUtils.getPlaceInfo(destinationPlaceId);
//
//        // Step 1: Fetch upcoming trips with seats available
//        List<Trip> trips = tripRepository.findUpcomingTripsWithSeatsAvailable(date);
//
//        // Step 2: Filter trips within radius
//        trips = trips.stream()
//                .filter(trip -> LocationUtils.isWithinRadius(userSource, trip.getSourceLocation(), radiusKm))
//                .filter(trip -> LocationUtils.isWithinRadius(userDest, trip.getDestinationLocation(), radiusKm))
//                .collect(Collectors.toList());
//
//        // Step 3: Compute match score
//        trips.forEach(trip -> {
//            double distanceScore = 1 - Math.min(LocationUtils.distanceKm(userSource, trip.getSourceLocation()) / radiusKm, 1.0);
//            double directionScore = DirectionUtils.isAligned(userSource, userDest, trip.getSourceLocation(), trip.getDestinationLocation()) ? 1.0 : 0.5;
//            double ratingScore = trip.getDriver().getRating() / 5.0;
//            double seatScore = (double) trip.getAvailableSeats() / trip.getTotalSeats();
//            double timeScore = date != null && trip.getDate().equals(date) ? 1.0 : 0.7;
//
//            double totalScore = (0.4 * distanceScore) +
//                                (0.2 * directionScore) +
//                                (0.2 * ratingScore) +
//                                (0.1 * seatScore) +
//                                (0.1 * timeScore);
//
//            trip.setMatchScore(totalScore);
//        });
//
//        // Step 4: Sort by score, then rating, then price
//        trips.sort(Comparator
//                .comparingDouble(Trip::getMatchScore).reversed()
//                .thenComparingDouble(t -> t.getDriver().getRating()).reversed()
//                .thenComparingDouble(Trip::getPrice));
//
//        return trips;
//    }
}
