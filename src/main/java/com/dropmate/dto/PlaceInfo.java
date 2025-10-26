package com.dropmate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceInfo {
	private String placeId;
	private String name;
	private String address;
	private double latitude;
	private double longitude;    
}