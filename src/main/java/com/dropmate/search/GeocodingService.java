package com.dropmate.search;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dropmate.dto.PlaceInfo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GeocodingService {

    @Value("${serverkey.google.maps.api.key:null}")
    private String apiKey;

    private static final String GEOCODE_API = "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s";
    private static final String PLACE_DETAILS_API = "https://maps.googleapis.com/maps/api/place/details/json?place_id=%s&key=%s";

    private final RestTemplate restTemplate;

    public GeocodingService() {
        this.restTemplate = new RestTemplate();
    }

    // ==========================
    // 1️. Get Coordinates by Place Name
    // ==========================
    public double[] getCoordinates(String placeName) throws Exception {
        if (placeName == null || placeName.isEmpty()) return new double[]{0.0, 0.0};

        String url = String.format(GEOCODE_API, URLEncoder.encode(placeName, StandardCharsets.UTF_8), apiKey);
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !"OK".equals(response.get("status"))) {
            log.warn("Geocode API failed for place: {}", placeName);
            return new double[]{0.0, 0.0};
        }

        Map<String, Object> location = extractLocation(response);
        return new double[]{
                (double) location.get("lat"),
                (double) location.get("lng")
        };
    }

    // ==========================
    // 2️. Get Coordinates by Place ID
    // ==========================
    public double[] getCoordinatesByPlaceId(String placeId) throws Exception {
        if (placeId == null || placeId.isEmpty()) return new double[]{0.0, 0.0};

        Map<String, Object> placeDetails = getPlaceDetails(placeId);
        if (placeDetails == null) return new double[]{0.0, 0.0};

        Map<String, Object> geometry = (Map<String, Object>) placeDetails.get("geometry");
        Map<String, Object> location = (Map<String, Object>) geometry.get("location");

        return new double[]{
                ((Number) location.get("lat")).doubleValue(),
                ((Number) location.get("lng")).doubleValue()
        };
    }

    // ==========================
    // 3️. Get Full Place Details (Name, Address, Coordinates)
    // ==========================
    public PlaceInfo getPlaceInfoByPlaceId(String placeId) {
        if (placeId == null || placeId.isEmpty()) return null;

        try {
            Map<String, Object> details = getPlaceDetails(placeId);
            if (details == null) return null;

            PlaceInfo info = new PlaceInfo();
            info.setPlaceId(placeId);
            info.setName((String) details.get("name"));
            info.setAddress((String) details.get("formatted_address"));

            Map<String, Object> geometry = (Map<String, Object>) details.get("geometry");
            Map<String, Object> location = (Map<String, Object>) geometry.get("location");

            info.setLatitude(((Number) location.get("lat")).doubleValue());
            info.setLongitude(((Number) location.get("lng")).doubleValue());
            return info;

        } catch (Exception e) {
            log.error("Failed to fetch PlaceInfo for placeId={}", placeId, e);
            return null;
        }
    }

    // ==========================
    // 4️. Reverse Geocode (Coordinates → Address)
    // ==========================
    public String getAddressFromCoordinates(double lat, double lng) {
        String url = String.format(
                "https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s",
                lat, lng, apiKey
        );

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || !"OK".equals(response.get("status"))) return null;

        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
        if (results.isEmpty()) return null;

        return (String) results.get(0).get("formatted_address");
    }

    // ==========================
    // 5️. Private Helper: Fetch Place Details from Google API
    // ==========================
    private Map<String, Object> getPlaceDetails(String placeId) throws Exception {
        String url = String.format(PLACE_DETAILS_API, placeId, apiKey);
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !"OK".equals(response.get("status"))) return null;

        Map<String, Object> result = (Map<String, Object>) response.get("result");
        return result;
    }

    // ==========================
    // 6️. Private Helper: Extract Coordinates from Geocode Response
    // ==========================
    private Map<String, Object> extractLocation(Map<String, Object> geocodeResponse) {
        List<Map<String, Object>> results = (List<Map<String, Object>>) geocodeResponse.get("results");
        if (results.isEmpty()) return Map.of("lat", 0.0, "lng", 0.0);

        Map<String, Object> geometry = (Map<String, Object>) results.get(0).get("geometry");
        Map<String, Object> location = (Map<String, Object>) geometry.get("location");
        return location;
    }
}
