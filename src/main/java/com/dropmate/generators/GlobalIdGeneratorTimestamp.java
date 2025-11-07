package com.dropmate.generators;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class GlobalIdGeneratorTimestamp {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random random = new Random();

    private static String randomPart(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUM.charAt(random.nextInt(ALPHANUM.length())));
        }
        return sb.toString();
    }

    public static String generateBookingId() {
        return "BOOK-" + dateFormat.format(new Date()) + "-" + randomPart(6); //BOOK-20251029-AX4P7L
    }

    public static String generateRideId() {
        return "RIDE-" + dateFormat.format(new Date()) + "-" + randomPart(6); //RIDE-20251029-BK9ZQ2
    }
}
