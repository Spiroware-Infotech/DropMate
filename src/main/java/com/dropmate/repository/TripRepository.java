package com.dropmate.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dropmate.entity.Trip;
import com.dropmate.enums.TripStatus;
import com.dropmate.enums.VehicleType;

@Repository
public interface TripRepository extends JpaRepository<Trip, String> {
	
	List<Trip> findByUser_UserId(Long userId);

	List<Trip> findByStatus(TripStatus status);

	List<Trip> findByVehicleType(VehicleType type);

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

}
