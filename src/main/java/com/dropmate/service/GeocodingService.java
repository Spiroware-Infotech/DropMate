package com.dropmate.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class GeocodingService {

    // ⚙️ Use your SERVER-SIDE API key (NOT the browser one)
	@Value("${serverkey.google.maps.api.key}")
    private String GOOGLE_API_KEY;

    /**
     * Get coordinates (latitude, longitude) for a given address using Google Geocoding API.
     *
     * @param address The address or location string (e.g., "Nellore, Andhra Pradesh, India")
     * @return double[2] array → [latitude, longitude]
     * @throws Exception if no coordinates found or request fails
     */
    public double[] getCoordinates(String address) throws Exception {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address cannot be null or empty");
        }

        // Encode the address to be URL-safe
        String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + encodedAddress + "&key=" + GOOGLE_API_KEY;

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        JSONObject json = new JSONObject(response);
        String status = json.getString("status");

        // ✅ Handle Google API status codes
        switch (status) {
            case "OK":
                JSONArray results = json.getJSONArray("results");
                JSONObject locationObj = results.getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONObject("location");
                double lat = locationObj.getDouble("lat");
                double lng = locationObj.getDouble("lng");
                return new double[]{lat, lng};

            case "ZERO_RESULTS":
                throw new Exception("No coordinates found for: " + address);

            case "OVER_QUERY_LIMIT":
                throw new Exception("Google API query limit exceeded. Try again later.");

            case "REQUEST_DENIED":
                String message = json.optString("error_message", "Request denied by Google API");
                throw new Exception("REQUEST_DENIED: " + message);

            default:
                throw new Exception("Geocoding failed with status: " + status);
        }
    }
}
