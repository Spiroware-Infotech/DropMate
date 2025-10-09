package com.dropmate.service;

import java.util.Optional;

import com.dropmate.entity.DriverProfile;
import com.dropmate.entity.User;

public interface DriverProfileService {

	Optional<User> findByUsername(String username);

	DriverProfile getDriverProfileById(Long userId);

	void saveDriverProfile(DriverProfile driverProfile);

}
