package com.dropmate.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import com.dropmate.enums.TripType;
import com.dropmate.enums.TripStatus;
import com.dropmate.enums.VehicleType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	@Column(length = 36)
	private String id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "vehicle_type", nullable = false)
	private VehicleType vehicleType;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "trip_type", nullable = false)
	private TripType tripType;

	@Column(name = "origin_name", columnDefinition = "TEXT")
	private String originName;

	@Column(name = "origin_point", columnDefinition = "POINT")
	private Point originPoint;

	@Column(name = "destination_name", columnDefinition = "TEXT")
	private String destinationName;

	@Column(name = "destination_point", columnDefinition = "POINT")
	private Point destinationPoint;

	@Column(name = "route_geom", columnDefinition = "LINESTRING")
	private LineString routeGeom;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@Column(name = "start_time")
	private LocalTime startTime;

	@Column(name = "seats_total")
	private Integer seatsTotal = 0;

	@Column(name = "seats_available")
	private Integer seatsAvailable = 0;

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
}
