package com.dropmate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceInfo {
	private String name;
	private String address;
	private double latitude;
	private double longitude;
}