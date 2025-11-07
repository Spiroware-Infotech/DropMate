package com.dropmate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dropmate.entity.DriverProfile;
import com.dropmate.entity.User;
import com.dropmate.enums.KycStatus;
import com.dropmate.enums.VehicleType;

@Repository
public interface DriverProfileRepository extends JpaRepository<DriverProfile, Long> {
	//Optional<DriverProfile> findByUser(User user);
    List<DriverProfile> findByVehicleType(VehicleType vehicleType);
    List<DriverProfile> findByKycStatus(KycStatus kycStatus);
    
	DriverProfile findUserById(Long userId);
	
}
