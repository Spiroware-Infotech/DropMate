package com.dropmate.enums;
public enum VehicleFareType {
    BIKE(1.5, 0.8, 0.15, 3.0),
    CAR(3.0, 1.2, 0.25, 5.0),
    VAN(4.0, 1.8, 0.35, 7.0),
    TRUCK(5.0, 2.0, 0.35, 8.0);

    private final double baseFare;
    private final double perKmRate;
    private final double perMinRate;
    private final double minFare;

    VehicleFareType(double baseFare, double perKmRate, double perMinRate, double minFare) {
        this.baseFare = baseFare;
        this.perKmRate = perKmRate;
        this.perMinRate = perMinRate;
        this.minFare = minFare;
    }

    public double getBaseFare() { return baseFare; }
    public double getPerKmRate() { return perKmRate; }
    public double getPerMinRate() { return perMinRate; }
    public double getMinFare() { return minFare; }
}
