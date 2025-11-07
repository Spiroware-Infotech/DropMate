package com.dropmate.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.dropmate.entity.Booking;
import com.dropmate.entity.Rides;
import com.dropmate.entity.Trip;
import com.dropmate.entity.User;
import com.dropmate.enums.BookingStatus;
import com.dropmate.enums.TripType;
import com.dropmate.repository.BookingRepository;
import com.dropmate.repository.RidesRepository;
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
	private final RidesRepository ridesRespository;
	
	public Booking createPassengerBooking(String tripId, Long userId, int seats) {
		
		// check if user already requested the same trip
//        boolean alreadyRequested = bookingRepository.existsByUser_IdAndTrip_IdAndStatus(
//                userId, tripId, BookingStatus.REQUESTED);
//
//        if (alreadyRequested) {
//            throw new IllegalStateException("You already have a pending booking request for this trip.");
//        }
        
		Rides rides = ridesRespository.findById(tripId).orElseThrow(() -> new RuntimeException("Trip not found"));

		if (rides.getAvailableSeats() < seats) {
			throw new RuntimeException("Not enough seats available");
		}

		rides.setAvailableSeats(rides.getAvailableSeats() - seats);

		BigDecimal pricePerSeat = BigDecimal.valueOf(rides.getPrice());
		BigDecimal price = pricePerSeat.multiply(BigDecimal.valueOf(seats));
		// Optional: round to 2 decimal places if it's a currency value
		price = price.setScale(2, RoundingMode.HALF_UP);
		
		Booking booking = new Booking();
		booking.setRides(rides);
		booking.setPassenger(new User());
		booking.getPassenger().setUserId(userId);
		booking.setType(TripType.PASSENGER);
		booking.setSeatsBooked(seats);
		booking.setPrice(price);
		booking.setStatus(BookingStatus.CONFIRMED);
		booking.setCreatedAt(LocalDateTime.now());
		booking.setUpdatedAt(LocalDateTime.now());
		ridesRespository.save(rides);
		return bookingRepository.save(booking);
	}

	public Booking getBookingById(String bookingId) {
		return bookingRepository.findById(bookingId).orElse(null);
	}

//	public Booking createDeliveryBooking(String tripId, Long userId, BigDecimal weight, BigDecimal price) {
//		Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Trip not found"));
//
//		if (trip.getCargoSlotsAvailable() <= 0) {
//			throw new RuntimeException("No cargo slots available");
//		}
//
//		trip.setCargoSlotsAvailable(trip.getCargoSlotsAvailable() - 1);
//
//		Booking booking = new Booking();
//		booking.setRides(trip);
//		booking.setUser(new User());
//		booking.getUser().setUserId(userId);
//		booking.setType(TripType.DELIVERY);
//		booking.setCargoWeightKg(weight);
//		booking.setPrice(price);
//		booking.setStatus(BookingStatus.CONFIRMED);
//
//		tripRepository.save(trip);
//		return bookingRepository.save(booking);
//	}
//
//	public void cancelBooking(String bookingId) {
//		Booking booking = bookingRepository.findById(bookingId)
//				.orElseThrow(() -> new RuntimeException("Booking not found"));
//
//		Trip trip = booking.getTrip();
//		if (booking.getType() == TripType.PASSENGER) {
//			trip.setAvailableSeats(trip.getAvailableSeats() + booking.getSeatsBooked());
//		} else {
//			trip.setCargoSlotsAvailable(trip.getCargoSlotsAvailable() + 1);
//		}
//		booking.setStatus(BookingStatus.CANCELLED);
//
//		tripRepository.save(trip);
//		bookingRepository.save(booking);
//	}
}
