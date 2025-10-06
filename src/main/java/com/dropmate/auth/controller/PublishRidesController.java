package com.dropmate.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	
	
	// Step 2a: Submit Passenger ride form
	@PostMapping("/ride")
	public String publishRide(@ModelAttribute TripRequest tripRequest, Model model) {
		tripService.createTrip(tripRequest);
		return "redirect:/trips"; // after publishing, go to trip list
	}

	// Step 2: Delivery form
	@GetMapping("/delivery")
	public String showDeliveryForm(Model model) {
		model.addAttribute("deliveryRequest", new DeliveryRequest());
		return "user/publish/delivery-form"; // delivery creation form
	}

	// Step 2a: Submit Delivery form
	@PostMapping("/delivery")
	public String publishDelivery(@ModelAttribute DeliveryRequest deliveryRequest, Model model) {
		deliveryService.createDeliveryTrip(deliveryRequest);
		return "redirect:/trips"; // after publishing, go to trip list
	}

	// Optional: Confirm page for Passenger ride
	@GetMapping("/ride/{tripId}/confirm")
	public String confirmRide(@PathVariable Long tripId, Model model) {
		model.addAttribute("trip", tripService.getTripById(tripId));
		return "user/publish/ride-confirm"; // confirmation page
	}

	// Optional: Confirm page for Delivery
	@GetMapping("/delivery/{deliveryId}/confirm")
	public String confirmDelivery(@PathVariable Long deliveryId, Model model) {
		model.addAttribute("delivery", deliveryService.getDeliveryById(deliveryId));
		return "user/publish/delivery-confirm"; // confirmation page
	}
}
