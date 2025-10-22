package com.dropmate.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequest {
	private Long driverId;
	private String originName;
	private String destinationName;
	private String startTime;
	private String endTime;
	private double cargoWeightKg;
	private Integer cargoSlots;
	private BigDecimal pricePerKg;
	private boolean isFragile;
	private String notes;
	
	private double distance;
	private long duration;
	private String summary;
	private String vehicleType; // e.g., "CAR"
	private String bookingType; // INSTANT OR REVIEW
	private String returnRideOption; //YES or LATER
	private String sourceJson; // store JSON as text
	private String destinationJson; // store JSON as text
}
