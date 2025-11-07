package com.dropmate.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripBookingDTO {

    private Long id;                         // booking ID
    private String tripId;                   // trip reference
    private Long passengerId;                // passenger user ID
    private Long driverId;                   // driver user ID

    private int bookedSeats;                 // how many seats booked
    private double totalFare;                // total fare amount
    private String paymentStatus;            // e.g. PENDING, PAID, FAILED
    private String bookingStatus;            // e.g. CONFIRMED, CANCELLED, COMPLETED

    private LocalDateTime bookingDate;       // when booked
    private String pickupPoint;              // pickup location text
    private String dropPoint;                // drop location text
    private double pickupLat;                // pickup latitude
    private double pickupLng;                // pickup longitude
    private double dropLat;                  // drop latitude
    private double dropLng;                  // drop longitude

    private String tripDate;                 // trip date (for display)
    private String vehicleType;              // CAR, BIKE, AUTO etc.
    private String driverName;               // for response display
    private String passengerName;            // for response display
    
}
