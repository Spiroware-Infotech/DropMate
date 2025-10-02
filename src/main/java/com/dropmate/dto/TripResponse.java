package com.dropmate.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripResponse {
    private String id;

    // Driver info
    private Long driverId;
    private String driverName;

    // Trip info
    private String vehicleType;
    private String originName;
    private String destinationName;

    // Date/Time
    private String startTime;  // formatted string for Thymeleaf (e.g. "2025-09-27 10:00")
    private String endTime;

    // Availability
    private int seatsTotal;
    private int seatsAvailable;
    private int cargoSlotsTotal;
    private int cargoSlotsAvailable;

    // Pricing
    private BigDecimal pricePerSeat;
    private BigDecimal pricePerKg;

    // Status
    private String status;
}
