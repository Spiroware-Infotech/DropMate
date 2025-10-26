package com.dropmate.utils;

import java.util.HashMap;
import java.util.Map;

public class CityNameNormalizer {

    private static final Map<String, String> CITY_ALIASES = new HashMap<>();

    static {
        CITY_ALIASES.put("bangalore", "bengaluru");
        CITY_ALIASES.put("bombay", "mumbai");
        CITY_ALIASES.put("madras", "chennai");
        CITY_ALIASES.put("calcutta", "kolkata");
        // add more if needed
    }

    public static String normalize(String name) {
        if (name == null) return null;
        String lower = name.toLowerCase().trim();
        return CITY_ALIASES.getOrDefault(lower, lower);
    }
}
