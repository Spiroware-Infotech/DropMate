package com.dropmate.generators;

import java.util.UUID;

public class GlobalIdGeneratorUUID {

    public static String generateBookingId() {
        return "BOOK-" + shortUUID(); //BOOK-3F9A1C2E
    }

    public static String generateRideId() {
        return "RIDE-" + shortUUID(); //RIDE-7D2B9F10
    }

    private static String shortUUID() {
        return UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }
}
