package com.dropmate.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dropmate.entity.DriverProfile;
import com.dropmate.entity.User;
import com.dropmate.entity.UserExperience;
import com.dropmate.enums.ExperienceLevel;
import com.dropmate.repository.DriverProfileRepository;
import com.dropmate.repository.RatingRepository;
import com.dropmate.repository.RidesRepository;
import com.dropmate.repository.UserExperienceRepository;
import com.dropmate.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final UserExperienceRepository experienceRepository;
    private final RidesRepository ridesRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final DriverProfileRepository profileRepository;
    
    /**
     * Calculate experience level based on completed rides and average rating
     */
    public ExperienceLevel calculateExperienceLevel(int ridesCompleted, double avgRating, boolean verified, boolean idVerified) {
        if (ridesCompleted >= 40 && avgRating >= 4.8 && verified && idVerified)
            return ExperienceLevel.AMBASSADOR;
        else if (ridesCompleted >= 20 && avgRating >= 4.6 && verified)
            return ExperienceLevel.ELITE_MEMBER;
        else if (ridesCompleted >= 10 && avgRating >= 4.3)
            return ExperienceLevel.EXPERIENCED_DRIVER;
        else if (ridesCompleted >= 6 && avgRating >= 4.2)
            return ExperienceLevel.TRUSTED_MEMBER;
        else if (ridesCompleted >= 3 && avgRating >= 4.0)
            return ExperienceLevel.RISING_STAR;
        else
            return ExperienceLevel.NEWCOMER;
    }

    /**
     * Recalculate experience for one user
     */
    @Transactional
    public void updateUserExperience(DriverProfile driver) {
    	User user = userRepository.findById(driver.getId()).orElse(null);
    	
        int ridesCompleted = ridesRepository.countByDriverAndStatus(user, "COMPLETED");
        
        Double avgRating = ratingRepository.findAverageRatingByUserId(driver.getId());
        boolean verified = driver.isEmailVerified() && driver.isPhoneVerified();
        boolean idVerified = driver.isIdVerified();

        UserExperience userExp = experienceRepository.findByDriver(driver)
                .orElseGet(() -> new UserExperience(driver));

        userExp.setRidesCompleted(ridesCompleted);
        userExp.setAverageRating(avgRating != null ? avgRating : 0.0);

        // Calculate total points
        int points = (ridesCompleted * 10) + (int)(avgRating * 10);
        userExp.setTotalPoints(points);

        ExperienceLevel level = calculateExperienceLevel(ridesCompleted, avgRating, verified, idVerified);
        userExp.setExperienceLevel(level);
        userExp.setLastUpdated(LocalDateTime.now());

        experienceRepository.save(userExp);
    }

    /**
     * Auto recalculate experience for all users daily at midnight
     */
    @Scheduled(cron = "0 0 0 * * *") // Every day at midnight
    @Transactional
    public void recalculateAllUsersExperience() {
        List<DriverProfile> allUsers = profileRepository.findAll();
        for (DriverProfile user : allUsers) {
            updateUserExperience(user);
        }
        log.info("✅ User experience levels updated for " + allUsers.size() + " users.");
    }
}
