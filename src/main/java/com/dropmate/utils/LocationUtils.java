package com.dropmate.utils;

import org.apache.commons.text.StringEscapeUtils;

import com.dropmate.dto.PlaceInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LocationUtils {
	private static final ObjectMapper mapper = new ObjectMapper();


    public static PlaceInfo getPlaceInfoDetails(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }

        try {
            // Clean if it’s double-encoded
            if (jsonString.startsWith("\"{") && jsonString.endsWith("}\"")) {
                jsonString = StringEscapeUtils.unescapeJson(jsonString);
                jsonString = jsonString.substring(1, jsonString.length() - 1); // remove outer quotes
            }

            return mapper.readValue(jsonString, PlaceInfo.class);
        } catch (Exception e) {
            System.err.println("Failed to parse JSON: " + jsonString);
            e.printStackTrace();
            return null;
        }
    }

    
 // From JSON string
    public static PlaceInfo getPlaceInfo(String json) {
        if (json == null || json.isEmpty()) return null;
        try {
            return mapper.readValue(json, PlaceInfo.class);
        } catch (Exception e) {
            return null;
        }
    }

    // From PlaceInfo object (safe copy)
    public static PlaceInfo getPlaceInfo(PlaceInfo place) {
        if (place == null) return null;
        try {
            String json = mapper.writeValueAsString(place);
            return mapper.readValue(json, PlaceInfo.class);
        } catch (Exception e) {
            return null;
        }
    }
    
	public static double getLatitude(String locationJson) {
		try {
			JsonNode node = mapper.readTree(locationJson);
			return node.get("latitude").asDouble();
		} catch (Exception e) {
			return 0;
		}
	}

	public static double getLongitude(String locationJson) {
		try {
			JsonNode node = mapper.readTree(locationJson);
			return node.get("longitude").asDouble();
		} catch (Exception e) {
			return 0;
		}
	}
	
	
	public static double distanceKm(PlaceInfo p1, PlaceInfo p2) {
        if (p1 == null || p2 == null) return Double.MAX_VALUE;
        return GeoUtils.distanceKm(p1.getLatitude(), p1.getLongitude(), p2.getLatitude(), p2.getLongitude());
    }

    public static boolean isWithinRadius(PlaceInfo p1, PlaceInfo p2, double radiusKm) {
        return distanceKm(p1, p2) <= radiusKm;
    }
}
