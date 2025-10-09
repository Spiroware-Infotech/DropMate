package com.dropmate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FareRequest {
    private String origin;
    private String destination;
    private double distance; // meters
    private double duration; // seconds
    private String vehicleType; // e.g., "CAR"
}
