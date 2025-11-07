package com.dropmate.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dropmate.entity.DriverProfile;
import com.dropmate.entity.User;
import com.dropmate.repository.DriverProfileRepository;
import com.dropmate.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DriverProfileServiceImpl implements DriverProfileService {

	private final UserRepository userRepository;
	private final DriverProfileRepository driverProfileRepository;

	@Override
	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}
	
	@Override
	public Optional<DriverProfile> findByUser(User user) {
		return null;
        //return driverProfileRepository.findByUser(user);
    }
	
	@Override
	public DriverProfile getDriverProfileById(Long userId) {
		return driverProfileRepository.findById(userId).orElse(null);
	}

	@Override
	public DriverProfile saveDriverProfile(DriverProfile driverProfile) {
		driverProfile.setCreatedAt(LocalDateTime.now());
		driverProfile.setUpdatedAt(LocalDateTime.now());
		return driverProfileRepository.save(driverProfile);
	}

	@Override
	public DriverProfile findById(Long userId) {
		return driverProfileRepository.findById(userId).orElse(null);
	}

}
