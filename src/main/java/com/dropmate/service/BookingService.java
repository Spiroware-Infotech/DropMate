package com.dropmate.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.dropmate.entity.Booking;
import com.dropmate.entity.Trip;
import com.dropmate.entity.User;
import com.dropmate.enums.BookingStatus;
import com.dropmate.enums.TripType;
import com.dropmate.repository.BookingRepository;
import com.dropmate.repository.TripRepository;
import com.dropmate.repository.TripStopRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingService {

	private final BookingRepository bookingRepository;
	private final TripRepository tripRepository;
	private final TripStopRepository tripStopRepository;

	public Booking createPassengerBooking(String tripId, Long userId, int seats, BigDecimal price) {
		Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Trip not found"));

		if (trip.getSeatsAvailable() < seats) {
			throw new RuntimeException("Not enough seats available");
		}

		trip.setSeatsAvailable(trip.getSeatsAvailable() - seats);

		Booking booking = new Booking();
		booking.setTrip(trip);
		booking.setBooker(new User());
		booking.getBooker().setUserId(userId);
		booking.setType(TripType.PASSENGER);
		booking.setSeatsBooked(seats);
		booking.setPrice(price);
		booking.setStatus(BookingStatus.CONFIRMED);

		tripRepository.save(trip);
		return bookingRepository.save(booking);
	}

	public Booking createDeliveryBooking(String tripId, Long userId, BigDecimal weight, BigDecimal price) {
		Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Trip not found"));

		if (trip.getCargoSlotsAvailable() <= 0) {
			throw new RuntimeException("No cargo slots available");
		}

		trip.setCargoSlotsAvailable(trip.getCargoSlotsAvailable() - 1);

		Booking booking = new Booking();
		booking.setTrip(trip);
		booking.setBooker(new User());
		booking.getBooker().setUserId(userId);
		booking.setType(TripType.DELIVERY);
		booking.setCargoWeightKg(weight);
		booking.setPrice(price);
		booking.setStatus(BookingStatus.CONFIRMED);

		tripRepository.save(trip);
		return bookingRepository.save(booking);
	}

	public void cancelBooking(String bookingId) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found"));

		Trip trip = booking.getTrip();
		if (booking.getType() == TripType.PASSENGER) {
			trip.setSeatsAvailable(trip.getSeatsAvailable() + booking.getSeatsBooked());
		} else {
			trip.setCargoSlotsAvailable(trip.getCargoSlotsAvailable() + 1);
		}
		booking.setStatus(BookingStatus.CANCELLED);

		tripRepository.save(trip);
		bookingRepository.save(booking);
	}
}
