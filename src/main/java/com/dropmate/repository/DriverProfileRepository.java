package com.dropmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dropmate.entity.DriverProfile;
import com.dropmate.enums.KycStatus;
import com.dropmate.enums.VehicleType;

@Repository
public interface DriverProfileRepository extends JpaRepository<DriverProfile, String> {
    List<DriverProfile> findByVehicleType(VehicleType vehicleType);
    List<DriverProfile> findByKycStatus(KycStatus kycStatus);
}
