package com.dropmate.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.dropmate.dto.DeliveryRequest;
import com.dropmate.dto.RideRequest;
import com.dropmate.service.DeliveryService;
import com.dropmate.service.TripService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/user/delivery")
@SessionAttributes("deliveryRequest") // store in session across all steps
@RequiredArgsConstructor
public class ItemDeliveryController {

	private final TripService tripService;
	private final DeliveryService deliveryService;
	
	@ModelAttribute("deliveryRequest")
	public DeliveryRequest deliveryRequest() {
		return new DeliveryRequest(); // initialize once
	}
	
	// Step 2: Delivery form
	@GetMapping("/departure")
	public String showDeliveryForm(@ModelAttribute("deliveryRequest") DeliveryRequest request, Model model) {
		
		return "user/delivery/delivery-departure"; // delivery creation form
	}
	
	@PostMapping("/arrival")
	public String arrival(@ModelAttribute("deliveryRequest") DeliveryRequest request, Model model) {
		
		return "user/delivery/delivery-arrival";
	}


	@PostMapping("/choose-your-route")
	public String chooseYourRoute(@ModelAttribute("deliveryRequest") DeliveryRequest request, Model model) {
		model.addAttribute("request", request);
		return "user/delivery/chooseYourRoute";
	}
	
	@GetMapping("/date")
	public String departureDate(@ModelAttribute("deliveryRequest") DeliveryRequest request, Model model) {
		return "user/delivery/delivery-date";
	}
	
	// Optional: Confirm page for Delivery
	@GetMapping("/delivery/{deliveryId}/confirm")
	public String confirmDelivery(@PathVariable Long deliveryId, Model model) {
		model.addAttribute("delivery", deliveryService.getDeliveryById(deliveryId));
		return "user/publish/delivery-confirm"; // confirmation page
	}
}
