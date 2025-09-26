package com.dropmate.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dropmate.entity.Trip;
import com.dropmate.entity.TripStop;
import com.dropmate.entity.User;
import com.dropmate.enums.TripStatus;
import com.dropmate.repository.TripRepository;
import com.dropmate.repository.TripStopRepository;
import com.dropmate.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TripService {

	private final TripRepository tripRepository;
	private final UserRepository userRepository;
	private final TripStopRepository tripStopRepository;

	
	public Trip createTrip(Long driverId, Trip trip, List<TripStop> stops) {
		User driver = userRepository.findById(driverId).orElseThrow(() -> new RuntimeException("Driver not found"));

		trip.setUser(driver);
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
}
