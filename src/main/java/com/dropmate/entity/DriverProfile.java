package com.dropmate.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.dropmate.enums.KycStatus;
import com.dropmate.enums.VehicleType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "driver_profiles")
public class DriverProfile {
	
	@Id
    @Column(name = "driver_id", length = 36)
    private Long id;
	
	@Column(name = "firstname")
	private String firstname;
	
	private String vehiclename;
	 
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "vehicle_number", length = 32)
    private String vehicleNumber;

    @Column(name = "seats_capacity")
    private Integer seatsCapacity = 1;

    @Column(name = "cargo_capacity_kg")
    private Integer cargoCapacityKg = 0;

    @Column(name = "cargo_slots")
    private Integer cargoSlots = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status")
    private KycStatus kycStatus = KycStatus.PENDING;

    @Column(name = "community_vouched")
    private Boolean communityVouched = false;

    @Column(name = "rating_avg")
    private Integer ratingAvg;

    @Column(name = "trips_count")
    private Integer tripsCount = 0;

    @Column(columnDefinition = "TEXT")
    private String details;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    private boolean isEmailVerified;
    private boolean isIdVerified;
    private boolean isPhoneVerified;
    
    // One driver can create many trips
    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Trip> trips = new ArrayList<>();
    
    @OneToOne(mappedBy = "driver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private UserExperience userExperience;
    
}
