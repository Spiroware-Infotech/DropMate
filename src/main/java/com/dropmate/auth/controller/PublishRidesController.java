package com.dropmate.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dropmate.dto.DeliveryRequest;
import com.dropmate.dto.TripRequest;
import com.dropmate.service.DeliveryService;
import com.dropmate.service.TripService;

@Controller
@RequestMapping("/user/publish")
public class PublishRidesController {

	private final TripService tripService;
	private final DeliveryService deliveryService;

	public PublishRidesController(TripService tripService, DeliveryService deliveryService) {
		this.tripService = tripService;
		this.deliveryService = deliveryService;
	}

	// Step 1: Select type (Passenger Ride or Delivery)
	@GetMapping("/select-type")
	public String selectTypePage() {
		return "user/publish/select-type"; // page with buttons: Passenger Ride / Delivery
	}

	
	// Optional: Confirm page for Passenger ride
	@GetMapping("/ride/{tripId}/confirm")
	public String confirmRide(@PathVariable Long tripId, Model model) {
		//model.addAttribute("trip", tripService.getTripById(tripId));
		return "user/publish/ride-confirm"; // confirmation page
	}


}
