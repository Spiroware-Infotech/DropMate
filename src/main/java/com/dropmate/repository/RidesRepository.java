package com.dropmate.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dropmate.entity.Rides;
import com.dropmate.entity.User;
import com.dropmate.enums.TripType;

@Repository
public interface RidesRepository  extends JpaRepository<Rides, String>{

	 // Fetch trips by date
    List<Rides> findByStartDate(LocalDate date);

    // Optionally filter by active trips
    List<Rides> findByStartDateAndIsActiveTrue(LocalDate date);
	
    List<Rides> findByTripType(TripType tripType);
    
    //List<Rides> findByDriverId(Long driverId);
    
   // Rides created by the user
    @Query("SELECT r FROM Rides r WHERE r.driver.userId = :userId")
    List<Rides> findRidesCreatedByUser(@Param("userId") Long userId);

    // Rides booked by the user (through bookings)
    @Query("SELECT DISTINCT b.rides FROM Booking b WHERE b.passenger.userId = :userId")
    List<Rides> findRidesBookedByUser(@Param("userId") Long userId);
    
    @Query("SELECT t FROM Rides t WHERE t.tripType = :tripType AND LOWER(t.status) = LOWER(:status)")
    List<Rides> findByTripTypeAndStatus(@Param("tripType") TripType tripType, @Param("status") String status);
    
    
    @Query(value = """
            SELECT * FROM rides r
            WHERE r.driver_id = :userId
            """, nativeQuery = true)
     List<Rides> findAllMyRides(@Param("userId") Long userId);
    
    @Query("""
            SELECT DISTINCT r FROM Rides r
            LEFT JOIN r.bookings b
            WHERE r.tripType = :tripType
              AND LOWER(r.status) = LOWER(:status)
              AND (r.driver.userId = :userId OR b.passenger.userId = :userId)
            """)
    List<Rides> findMyPassengerRides(
            @Param("tripType") TripType tripType,
            @Param("status") String status,
            @Param("userId") Long userId);
    
    
    int countByDriverAndStatus(User user, String status);
    
}
