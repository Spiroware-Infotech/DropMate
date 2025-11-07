package com.dropmate.generators;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomIdGenerator {

    private static final AtomicInteger counter = new AtomicInteger(1);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public static synchronized String generateBookingId() {
        String datePart = dateFormat.format(new Date());
        int seq = counter.getAndIncrement();
        return String.format("BK-%s-%05d", datePart, seq);
    }

    public static synchronized String generateRideId() {
        String datePart = dateFormat.format(new Date());
        int seq = counter.getAndIncrement();
        return String.format("RIDE-%s-%05d", datePart, seq);
    }
}
