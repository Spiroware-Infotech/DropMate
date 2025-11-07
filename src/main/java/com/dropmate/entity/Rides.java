package com.dropmate.entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.dropmate.enums.TripStatus;
import com.dropmate.enums.TripType;
import com.dropmate.enums.VehicleType;
import com.dropmate.generators.GlobalIdGeneratorULID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "rides")
public class Rides {

    @Id
    private String id;

    @Column(name = "source", columnDefinition = "TEXT")
	private String source;
    
    @Column(name = "destination", columnDefinition = "TEXT")
   	private String destination;
    
    // =====================
    // Source & Destination as JSON
    // =====================
    @Lob
    @Column(columnDefinition = "TEXT")
    private String sourceJson;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String destinationJson;

    // =====================
    // Trip Details
    // =====================
    private LocalDate startDate;          // Trip date
    private LocalTime startTime;          // Trip start time
	private Integer totalSeats;			  // Total Seats
    private Integer availableSeats;       // Seats available
    private Double price;                 // Optional fare per seat
    
    @Enumerated(EnumType.STRING)
	@Column(name = "vehicle_type", nullable = false)
	private VehicleType vehicleType;

	@Enumerated(EnumType.STRING)
	@Column(name = "trip_type", nullable = false)
	private TripType tripType;
	
    private double distanceKm;            // Optional pre-calculated distance

    // =====================
    // Status Flags
    // =====================
    @Enumerated(EnumType.STRING)
    private TripStatus status = TripStatus.SCHEDULED;

    private Boolean isActive = true;      // For soft delete or cancel
    private String bookingType;
	private long durationInSec;
	private String comment;
	
    // =====================
    // Timestamps
    // =====================
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    
    // =====================
    // Driver / User Info
    // =====================
    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driver;
    
    @OneToMany(mappedBy = "rides", cascade = CascadeType.ALL)
    private List<Booking> bookings;
    
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = GlobalIdGeneratorULID.generateRideId();
        }
    }
    
}
