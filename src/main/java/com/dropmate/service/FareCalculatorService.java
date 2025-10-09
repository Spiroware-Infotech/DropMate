package com.dropmate.service;
import org.springframework.stereotype.Service;

import com.dropmate.enums.VehicleFareType;

@Service
public class FareCalculatorService {

    public double calculateFare(double distanceMeters, double durationSeconds, VehicleFareType vehicleType) {
        double distanceKm = distanceMeters / 1000.0;
        double durationMin = durationSeconds / 60.0;

        double fare = vehicleType.getBaseFare()
                + (distanceKm * vehicleType.getPerKmRate())
                + (durationMin * vehicleType.getPerMinRate());

        // apply minimum fare rule
        if (fare < vehicleType.getMinFare()) {
            fare = vehicleType.getMinFare();
        }

        return Math.round(fare * 100.0) / 100.0;
    }
}
