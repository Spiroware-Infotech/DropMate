package com.dropmate.repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dropmate.entity.DriverProfile;
import com.dropmate.entity.Trip;
import com.dropmate.entity.User;
import com.dropmate.enums.TripStatus;
import com.dropmate.enums.TripType;
import com.dropmate.enums.VehicleType;

@Repository
public interface TripRepository extends JpaRepository<Trip, String> {

    // 1️. Get all trips by driver object
    List<Trip> findByDriver(DriverProfile driver);

    // 2️. Get all trips by driver ID
    List<Trip> findByDriverId(Long driverId);

    // 3️. Optional: get trips by driver ID and status
    List<Trip> findByDriverIdAndStatus(Long driverId, String status);

    // 4️. Optional: get trips by driver and specific date
    //List<Trip> findByDriverIdAndStartDate(LocalDate startDate);
	 
	List<Trip> findByStatus(TripStatus status);

	List<Trip> findByVehicleType(VehicleType type);

	List<Trip> findByTripType(TripType tripType);
	
	@Query("SELECT t FROM Trip t WHERE t.tripType = :tripType AND LOWER(t.status) = LOWER(:status)")
    List<Trip> findByTripTypeAndStatus(@Param("tripType") TripType tripType, @Param("status") String status);

	@Query("SELECT t FROM Trip t WHERE t.tripType = :tripType")
	List<Trip> findByTripType(@Param("tripType") String tripType);
	 
	@Query("SELECT t FROM Trip t WHERE t.startTime >= :from AND t.startTime <= :to")
	List<Trip> findTripsBetween(@Param("from") Timestamp from, @Param("to") Timestamp to);

	// Custom native query using PostGIS for location + time filtering
	@Query(value = """
			SELECT t.*
			FROM trips t
			WHERE t.start_time BETWEEN :from AND :to
			  AND ST_DWithin(
			      ST_SetSRID(ST_GeomFromText(:originWkt), 4326),
			      t.origin_location,
			      :radius
			  )
			  AND t.status = 'SCHEDULED'
			ORDER BY t.start_time ASC
			""", nativeQuery = true)
	List<Trip> findUpcomingTripsNear(@Param("from") Timestamp from, @Param("to") Timestamp to,
			@Param("originWkt") String originWkt, @Param("radius") double radius);

	List<Trip> findByStartDate(LocalDate date);

}
