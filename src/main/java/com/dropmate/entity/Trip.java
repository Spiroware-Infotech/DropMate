package com.dropmate.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.dropmate.enums.TripStatus;
import com.dropmate.enums.TripType;
import com.dropmate.enums.VehicleType;
import com.dropmate.generators.GlobalIdGeneratorULID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
@Table(name = "trips")
public class Trip {

	@Id
	private String id;

	@Enumerated(EnumType.STRING)
	@Column(name = "vehicle_type", nullable = false)
	private VehicleType vehicleType;

	@Enumerated(EnumType.STRING)
	@Column(name = "trip_type", nullable = false)
	private TripType tripType;

	@Column(name = "origin_name", columnDefinition = "TEXT")
	private String originName;
	
	@Column(name = "src_placeId", columnDefinition = "TEXT")
	private String sourcePlaceId;
	
	@Column(name = "sourceJson", columnDefinition = "JSON")
	private String sourceJson;

	@Column(name = "dest_placeId", columnDefinition = "TEXT")
	private String destinationPlaceId;
	 
	@Column(name = "destination_name", columnDefinition = "TEXT")
	private String destinationName;

	@Column(name = "destinationJson", columnDefinition = "JSON")
	private String destinationJson;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@Column(name = "start_time")
	private LocalTime startTime;

	@Column(name = "total_seats")
	private Integer totalSeats = 0;

	@Column(name = "available_seats")
	private Integer availableSeats = 0;

	@Column(name = "cargo_slots_total")
	private Integer cargoSlotsTotal = 0;

	@Column(name = "cargo_slots_available")
	private Integer cargoSlotsAvailable = 0;

	@Column(name = "price_per_seat", precision = 10, scale = 2)
	private BigDecimal pricePerSeat;

	@Column(name = "price_per_kg", precision = 10, scale = 2)
	private BigDecimal pricePerKg;

	@Column(name = "is_recurring")
	private Boolean isRecurring = false;

	@Column(name = "recurring_rule", columnDefinition = "JSON")
	private String recurringRule;

	@Enumerated(EnumType.STRING)
	private TripStatus status = TripStatus.SCHEDULED;

	@Column(columnDefinition = "JSON")
	private String extra;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	private String bookingType;
	private long duration;
	private double distance;
	private String comment;
	
	private Boolean isActive = true;      // For soft delete or cancel
	 
	// Each trip is created by one driver
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private DriverProfile driver;
    
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = GlobalIdGeneratorULID.generateRideId();
        }
    }
}
