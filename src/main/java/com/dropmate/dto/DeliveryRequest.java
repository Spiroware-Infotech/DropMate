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
}
