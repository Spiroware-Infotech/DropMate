package com.dropmate.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dropmate.entity.Rides;

@Repository
public interface RidesRespository  extends JpaRepository<Rides, String>{

	 // Fetch trips by date
    List<Rides> findByStartDate(LocalDate date);

    // Optionally filter by active trips
    List<Rides> findByStartDateAndIsActiveTrue(LocalDate date);
	
}
