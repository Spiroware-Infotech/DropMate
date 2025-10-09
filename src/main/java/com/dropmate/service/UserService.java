package com.dropmate.service;

import java.util.List;
import java.util.Optional;

import com.dropmate.dto.RegistrationDto;
import com.dropmate.entity.DriverProfile;
import com.dropmate.entity.User;

public interface UserService {
	User saveUser(RegistrationDto registrationDto);

	Optional<User> findByEmail(String email);

	Optional<User> findByUsername(String username);

	List<User> findAll();

	User save(User user);

	void delete(User user);

	User getCurrentUser();

	boolean existsByUsername(String username);

	List<User> findByAllUsers();

	// verify user
	public boolean verify(String verificationCode);

	
}
