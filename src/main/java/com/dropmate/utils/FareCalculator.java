package com.dropmate.utils;
public class FareCalculator {

    private static final double BASE_FARE = 3.0;
    private static final double PER_KM_RATE = 1.2;
    private static final double PER_MIN_RATE = 0.25;
    private static final double MIN_FARE = 5.0;

    public static double calculateFare(double distanceMeters, double durationSeconds, double surgeMultiplier) {
        double distanceKm = distanceMeters / 1000.0;
        double durationMin = durationSeconds / 60.0;

        double fare = BASE_FARE + (distanceKm * PER_KM_RATE) + (durationMin * PER_MIN_RATE);
        if (fare < MIN_FARE) fare = MIN_FARE;

        fare *= surgeMultiplier;
        return Math.round(fare * 100.0) / 100.0;
    }
}
