package com.dropmate.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class TripResponse {
	private String id;
	private LocalDate tripDate;
	private String startTime;
	private String endTime;
	private String startLocation;
	private String endLocation;
	private String status;
	private String bookingType;
	private String duration;
	private String distance;

}
