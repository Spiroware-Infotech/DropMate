package com.dropmate.auth.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dropmate.dto.TripRequest;
import com.dropmate.dto.TripResponse;
import com.dropmate.service.TripService;

@Controller
@RequestMapping("/user/trip")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping(value = "/details")
    public String tripById(Model model) {
        List<TripResponse> trips = tripService.getAllTrips();
        model.addAttribute("trips", trips);
        return "user/trip/ride-details"; 
    }
    
    // 1. Show all trips
    @GetMapping
    public String listTrips(Model model) {
        List<TripResponse> trips = tripService.getAllTrips();
        model.addAttribute("trips", trips);
        return "trip/list"; 
    }

    // 2. Show create trip form
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("tripRequest", new TripRequest());
        return "trip/create"; // trip/create.html
    }

    // 3. Handle trip creation
    @PostMapping("/create")
    public String createTrip(@ModelAttribute TripRequest tripRequest) {
        tripService.createTrip(tripRequest);
        return "redirect:/trips";
    }

    // 4. Show update trip form
    @GetMapping("/{tripId}/edit")
    public String showEditForm(@PathVariable Long tripId, Model model) {
        TripResponse trip = tripService.getTripById(tripId);
        model.addAttribute("tripRequest", trip);
        return "trip/edit"; // trip/edit.html
    }

    // 5. Handle trip update
    @PostMapping("/{tripId}/edit")
    public String updateTrip(@PathVariable Long tripId,
                             @ModelAttribute TripRequest tripRequest) {
        tripService.updateTrip(tripId, tripRequest);
        return "redirect:/trips";
    }

    // 6. Delete trip
    @GetMapping("/{tripId}/delete")
    public String deleteTrip(@PathVariable Long tripId) {
        tripService.deleteTrip(tripId);
        return "redirect:/trips";
    }

    // 7. Trip details page
    @GetMapping("/{tripId}")
    public String getTrip(@PathVariable Long tripId, Model model) {
        TripResponse trip = tripService.getTripById(tripId);
        model.addAttribute("trip", trip);
        return "trip/detail"; // trip/detail.html
    }

    // 8. Search trips (simple form -> results)
    @GetMapping("/search")
    public String searchTrips(@RequestParam String from,
                              @RequestParam String to,
                              @RequestParam(required = false) String date,
                              Model model) {
        List<TripResponse> trips = tripService.searchTrips(from, to, date);
        model.addAttribute("trips", trips);
        return "trip/search-results"; // trip/search-results.html
    }
}
