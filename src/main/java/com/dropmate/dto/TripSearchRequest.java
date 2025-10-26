package com.dropmate.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripSearchRequest {
	private String source;         // User typed or selected source name
	private String destination;    // User typed or selected destination name
	private String sourcePlaceId;  // from Google Autocomplete (recommended)
	private String destPlaceId;    // from Google Autocomplete (recommended)
	private LocalDate date;
	private Integer requiredSeats;
}
