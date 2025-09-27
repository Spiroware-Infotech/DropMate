package com.dropmate.dto;

import lombok.Data;

@Data
public class TripRequest {

    // Driver ID (hidden field in form or passed from session)
    private Long driverId;

    // Vehicle type
    private String vehicleType; // bike, car, van, truck, etc.

    // Trip locations
    private String originName;
    private String destinationName;

    // Date/Time
    private String startTime; // e.g., "2025-09-27 10:00"
    private String endTime;   // optional

    // Seats / cargo
    private int seatsTotal;
    private int seatsAvailable;
    private int cargoSlotsTotal;
    private int cargoSlotsAvailable;

    // Pricing
    private double pricePerSeat;
    private double pricePerKg;

    // Recurring
    private boolean isRecurring;
    private String recurringRule; // JSON string or simple text for weekly, daily, etc.

    // Status (optional, mostly for admin or updates)
    private String status;
}
