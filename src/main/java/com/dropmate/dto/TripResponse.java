package com.dropmate.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class TripResponse {
	private String id;
	private LocalDate tripDate;
	private String startTime;
	private String endTime;
	private BigDecimal pricePerSeat;
	private String startLocation;
	private String sourcePlaceId;
	private String endLocation;
	private String destinationPlaceId;
	private String status;
	private String bookingType;
	private String duration;
	private String distance;
	private String sourceJson;
	private String destinationJson;
	private PlaceInfo source;
	private PlaceInfo destination;
	private String driverName;
	private Integer availableSeats;
	
}
