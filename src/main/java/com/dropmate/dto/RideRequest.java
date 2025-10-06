package com.dropmate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideRequest {
	private String departure;
	private String arrival;
	private String route;
	private String departureDate;
	private String departureTime;
	private int seats;
	private double price;
	private String comment;

	private double distance;
	private double duration;
	private String summary;
}
