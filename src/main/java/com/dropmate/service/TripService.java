package com.dropmate.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dropmate.dto.TripRequest;
import com.dropmate.dto.TripResponse;
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

		trip.setId(UUID.randomUUID().toString());
		trip.setUser(driver);
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
		// TODO Auto-generated method stub
		return null;
	}
}
