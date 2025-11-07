package com.dropmate.service;

import java.util.Optional;

import com.dropmate.entity.DriverProfile;
import com.dropmate.entity.User;

public interface DriverProfileService {

	Optional<User> findByUsername(String username);

	Optional<DriverProfile> findByUser(User user);

	DriverProfile getDriverProfileById(Long userId);

	DriverProfile saveDriverProfile(DriverProfile driverProfile);

	DriverProfile findById(Long userId);

}
