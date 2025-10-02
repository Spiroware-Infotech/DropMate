// MapController.java
package com.dropmate.map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

@Controller
public class MapController {
    
    private final GoogleMapsService googleMapsService;
    
    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;
    
    public MapController(GoogleMapsService googleMapsService) {
        this.googleMapsService = googleMapsService;
    }
    
    @GetMapping("/map")
    public String home(Model model) {
        model.addAttribute("searchRequest", new SearchRequest());
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        return "mapView";
    }
    
    @PostMapping("/map/search")
    public String searchPlaces(@Valid @ModelAttribute SearchRequest searchRequest, 
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("googleMapsApiKey", googleMapsApiKey);
            return "mapView";
        }
        
        try {
            var placesResponse = googleMapsService.searchPlaces(searchRequest).block();
            model.addAttribute("places", placesResponse.getResults());
            model.addAttribute("searchRequest", searchRequest);
            model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        } catch (Exception e) {
            model.addAttribute("error", "Error searching places: " + e.getMessage());
        }
        
        return "mapView";
    }
    
    @GetMapping("/map/nearby")
    public String searchNearby(@RequestParam String lat,
                              @RequestParam String lng,
                              @RequestParam(defaultValue = "5000") Integer radius,
                              @RequestParam(required = false) String type,
                              Model model) {
        try {
            var placesResponse = googleMapsService.searchNearby(lat, lng, radius, type).block();
            model.addAttribute("places", placesResponse.getResults());
            model.addAttribute("searchRequest", new SearchRequest());
            model.addAttribute("googleMapsApiKey", googleMapsApiKey);
            model.addAttribute("currentLocation", lat + "," + lng);
        } catch (Exception e) {
            model.addAttribute("error", "Error searching nearby places: " + e.getMessage());
        }
        
        return "mapView";
    }
}