package com.dropmate.entity;
import java.time.LocalDate;
import java.time.LocalTime;

import com.dropmate.dto.PlaceInfo;
import com.dropmate.enums.TripStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rides")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rides {

    @Id
    @Column(length = 36)
    private String id;

    // =====================
    // Driver / User Info
    // =====================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private DriverProfile driver; // driver

    @Column(name = "source", columnDefinition = "TEXT")
	private String source;
    
    @Column(name = "destination", columnDefinition = "TEXT")
   	private String destination;
    
    // =====================
    // Source & Destination as JSON
    // =====================
    @Column(columnDefinition = "jsonb", nullable = false)
    private PlaceInfo sourceJson;

    @Column(columnDefinition = "jsonb", nullable = false)
    private PlaceInfo destinationJson;

    // =====================
    // Trip Details
    // =====================
    private LocalDate startDate;          // Trip date
    private LocalTime startTime;          // Trip start time
    private Integer availableSeats;       // Seats available
    private Double price;                 // Optional fare per seat
    private String vehicleType;           // CAR, BIKE, VAN, etc.
    private Double distanceKm;            // Optional pre-calculated distance

    // =====================
    // Status Flags
    // =====================
    @Enumerated(EnumType.STRING)
    private TripStatus status = TripStatus.SCHEDULED;

    private Boolean isActive = true;      // For soft delete or cancel
    private String bookingType;
	private long durationInSec;
	private String comment;
	 private Double distanceFromUser;
    // =====================
    // Timestamps
    // =====================
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }
}
