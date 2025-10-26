package com.dropmate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideRequest {
	private String departure;
	private String sourcePlaceId;
	private String arrival;
	private String destinationPlaceId;
	private String route;
	private String departureDate;
	private String departureTime;
	private int seats;
	private double price;
	private String comment;
	private String sourceJson; // store JSON as text
	private String destinationJson; // store JSON as text
	
	private double distance;
	private long duration;
	private String summary;
	private String vehicleType; // e.g., "CAR"
	private String bookingType; // INSTANT OR REVIEW
	private String returnRideOption; //YES or LATER
}
