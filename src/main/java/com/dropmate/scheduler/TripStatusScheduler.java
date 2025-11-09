package com.dropmate.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dropmate.entity.Trip;
import com.dropmate.enums.TripStatus;
import com.dropmate.repository.TripRepository;

@Component
public class TripStatusScheduler {

	private static final Logger logger = LoggerFactory.getLogger(TripStatusScheduler.class);
	
	@Autowired
    private TripRepository tripRepository;

    /**
     * Runs every day at 12:05 AM
     * Cron format: second minute hour day month weekday
     * -> 0 5 0 * * * means 12:05 AM daily
     */
    //@Scheduled(cron = "0 0/5 * * * *")
    @Scheduled(cron = "0 5 0 * * *")
    public void updateTripStatuses() {
        LocalDate today = LocalDate.now();
        logger.info("🚀 Rides Scheduler started at " + today);

        // ✅ Get all trips from DB
        List<Trip> allTrips = tripRepository.findAll();
        logger.info("Rides Size: {}" , allTrips.size());
        //optimize by loading only relevant trips
        //List<Trip> trips = tripRepository.findByStatusIn(List.of(TripStatus.UPCOMING, TripStatus.ONGOING));

        int ongoingCount = 0;
        int archivedCount = 0;

        for (Trip trip : allTrips) {
            if (trip.getStartDate() == null) continue;

            // ✅ If trip date is today → set to ONGOING
            if (trip.getStartDate().isEqual(today) &&
                trip.getStatus() != TripStatus.ONGOING) {

                trip.setStatus(TripStatus.ONGOING);
                ongoingCount++;
            }

            // ✅ If trip date is before today → set to ARCHIVED
            else if (trip.getStartDate().isBefore(today) &&
                     trip.getStatus() != TripStatus.ARCHIVED &&
                     trip.getStatus() != TripStatus.COMPLETED &&
                     trip.getStatus() != TripStatus.CANCELLED) {

                trip.setStatus(TripStatus.ARCHIVED);
                trip.setIsActive(false);
                archivedCount++;
            }
        }

        // ✅ Save only if any trips were updated
        if (ongoingCount > 0 || archivedCount > 0) {
            tripRepository.saveAll(allTrips);
            logger.info("✅ Updated " + ongoingCount + " rides to ONGOING and " +
                               archivedCount + " rides to ARCHIVED");
        } else {
        	logger.info("ℹ️ No ride updates required today");
        }

        logger.info("🏁 Ride Scheduler completed.");
    }
}