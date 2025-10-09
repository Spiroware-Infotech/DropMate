package com.dropmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dropmate.entity.Booking;
import com.dropmate.enums.BookingStatus;
import com.dropmate.enums.TripType;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    List<Booking> findByTrip_Id(String tripId);
    List<Booking> findByBooker_UserId(Long userId);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByType(TripType type);
    List<Booking> findByBooker_UserIdAndStatus(Long userId, BookingStatus status);
}
