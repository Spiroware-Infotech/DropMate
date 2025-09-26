package com.dropmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dropmate.entity.TripStop;

@Repository
public interface TripStopRepository extends JpaRepository<TripStop, String> {
    List<TripStop> findByTrip_IdOrderByStopOrderAsc(String tripId);
}
