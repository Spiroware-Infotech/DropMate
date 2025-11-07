package com.dropmate.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.dropmate.enums.BookingStatus;
import com.dropmate.enums.PaymentStatus;
import com.dropmate.enums.TripType;
import com.dropmate.generators.GlobalIdGeneratorTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "bookings")
public class Booking {
	
    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private TripType type;

    private Integer seatsBooked = 0;
    private BigDecimal cargoWeightKg = BigDecimal.ZERO;

    @Column(columnDefinition = "json")
    private String cargoVolume;

    @ManyToOne
    @JoinColumn(name = "pickup_stop_id")
    private TripStop pickupStop;

    @ManyToOne
    @JoinColumn(name = "drop_stop_id")
    private TripStop dropStop;

    private BigDecimal price = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.REQUESTED;

    @Column(columnDefinition = "json")
    private String metadata;

    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relations
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryItem> deliveryItems = new ArrayList<>();
  
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id")
    private Rides rides;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User passenger;
    
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = GlobalIdGeneratorTimestamp.generateBookingId();
        }
    }
}

