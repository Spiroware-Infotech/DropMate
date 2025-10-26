package com.dropmate.search;
import java.time.LocalDate;
import java.time.LocalTime;

import com.dropmate.dto.PlaceInfo;
import com.dropmate.enums.TripStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RidesResponse {

    private String rideId;                // Trip database ID
    private String driverName;            // Driver's full name
    private Long driverId;                // Optional: driver user id
    private String source;             	  // Source details
    private String destination;           // Destination details
    private PlaceInfo sourceJson;         // Source details
    private PlaceInfo destinationJson;    // Destination details
    private LocalDate startDate;          // Trip date
    private LocalTime startTime;          // Trip start time
    private Integer availableSeats;       // Seats available
    private Double price;                 // Fare per seat
    private String vehicleType;           // CAR, BIKE, VAN, etc.
    private Double distanceFromUser;      // Distance from user's source (km)
    private TripStatus status;            // SCHEDULED, ONGOING, COMPLETED
    private Boolean isActive;             // Soft-delete flag

    // Optional: additional info
    private String vehicleNumber;         // Vehicle registration number
    private Double totalDistanceKm;       // Total trip distance
    private Double estimatedArrivalTime;  // ETA in hours
}
