package com.dropmate.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dropmate.config.SecurityUtil;
import com.dropmate.dto.RegistrationDto;
import com.dropmate.entity.DriverProfile;
import com.dropmate.entity.PublicUser;
import com.dropmate.entity.Role;
import com.dropmate.entity.User;
import com.dropmate.repository.PublicUserRepository;
import com.dropmate.repository.RoleRepository;
import com.dropmate.repository.UserRepository;
import com.dropmate.utils.UsernameUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final PublicUserRepository publicUserRepository;
	
	
	@Override
	public User saveUser(RegistrationDto registrationDto) {
		log.info("UserServiceImpl ---> saveUser ");
		User user = new User();
		user.setFirstname(registrationDto.getName());
		user.setUsername(UsernameUtil.generateUsername(registrationDto.getName()));
		user.setEmail(registrationDto.getEmail());
		user.setPhone(registrationDto.getPhone());
		user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
		Role role = roleRepository.findByName("ROLE_PASSENGER").isPresent() ? roleRepository.findByName("ROLE_PASSENGER").get()
				: null;
		Set<Role> userRoles = new HashSet<>();
		userRoles.add(role);
		user.setRoles(userRoles);
		user.setCreatedAt(LocalDateTime.now());
		user.setIsVerified(true);

		User info = userRepository.save(user);

		PublicUser publicUser = new PublicUser();
		publicUser.setUser(user);
		publicUser.setFirstname(info.getFirstname());
		publicUser.setLastname(info.getLastname());
		publicUser.setEmail(info.getEmail());
		publicUser.setPhone(info.getPhone());
		publicUser.setCreateddate(new Date());
		publicUser.setLastUpdateddate(new Date());
		publicUser.setCurrentStatus("INACTIVE");
		publicUser.setDob(registrationDto.getDob());
		
		publicUser.setUser(info);

		publicUserRepository.save(publicUser);

		return user;
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}

	@Override
	public void delete(User user) {
		userRepository.delete(user);
	}

	@Override
	public User getCurrentUser() {
		String username = SecurityUtil.getSessionUser();
		Optional<User> user = findByUsername(username);
		if (user.isPresent()) {
			return user.get();
		}
		return null;
	}

	@Override
	public boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}

	@Override
	public List<User> findByAllUsers() {
		return findAll();
	}
	
	// verify user by verification code
	@Override
	public boolean verify(String verificationCode) {
//		User user = userRepository.findByVerificationCode(verificationCode);
//
//		if (user == null || user.isEnabled()) {
//			return false;
//		} else {
//			// user.setVerificationCode(null);
//			user.setActive(true);
//			userRepository.save(user);
//
//			// update UsersDetails account status
//			Student student = studentRepository.findById(user.getUserId()).orElse(null);
//			School school = schoolRepository.findById(user.getUserId()).orElse(null);
//			if (student != null && student instanceof Student) {
//				student.setCurrentStatus("ACTIVE");
//				student.setLastUpdateddate(new Date());
//				studentRepository.save(student);
//			} else if (school != null && school instanceof School) {
//				school.setCurrentStatus("ACTIVE");
//				school.setLastUpdateddate(new Date());
//				schoolRepository.save(school);
//				
//				//update default settings 
//				GeneralSettings generalSettings = settingRepository.findSettingsBySchoolId(school.getId());
//				if (Objects.isNull(generalSettings)) {
//					generalSettings = new GeneralSettings();
//					generalSettings.setThemeColour("default");
//				}
//				
//				 settingRepository.save(generalSettings);
//				 
//			} else {
//				return false;
//			}

			return true;
		}

	
}
