package com.dropmate.generators;
import de.huxhorn.sulky.ulid.ULID;

public class GlobalIdGeneratorULID {
    private static final ULID ulid = new ULID();

    public static String generateBookingId() {
        return "BOOK-" + ulid.nextULID(); // BOOK-01JF6Q2CZXK9J8D9ZSPAW2NR7A
    }

    public static String generateRideId() {
        return "RIDE-" + ulid.nextULID(); // RIDE-01JF6Q2CZXMNT3TJP7QXH1SV1F
    }
}
