package com.dropmate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FareResponse {
	private String vehicleType;
	private double totalFare;
	private String currency;
}
