package com.dropmate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dropmate.entity.DriverProfile;
import com.dropmate.entity.UserExperience;

public interface UserExperienceRepository extends JpaRepository<UserExperience, Long> {
    Optional<UserExperience> findByDriver(DriverProfile driver);
}