package com.dropmate.auth.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.dropmate.controller.CommonController;
import com.dropmate.dto.RideRequest;
import com.dropmate.entity.Rides;
import com.dropmate.entity.User;
import com.dropmate.enums.VehicleFareType;
import com.dropmate.service.DriverProfileService;
import com.dropmate.service.FareCalculatorService;
import com.dropmate.service.RidesServices;
import com.dropmate.service.TripService;
import com.dropmate.utils.FareCalculator;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/user/ride")
@SessionAttributes("tripRequest") // store in session across all steps
public class RideController extends CommonController{

	@Autowired
	private FareCalculatorService fareCalculatorService;

	@Autowired
	DriverProfileService driverProfileService;

	@Autowired
	TripService tripService;

	@Autowired
	private RidesServices ridesServices;
	
	@Autowired
	private ObjectMapper mapper;
	 

	@ModelAttribute("tripRequest")
	public RideRequest tripRequest() {
		return new RideRequest(); // initialize once
	}

	@GetMapping
	public String showRideForm(@ModelAttribute("tripRequest") RideRequest tripRequest, Model model) {
		return "user/publish/ride-form";
	}

	@GetMapping("/departure")
	public String departure(@ModelAttribute("tripRequest") RideRequest trip, Model model) {
		model.addAttribute("GOOGLE_MAPS_API_KEY", GOOGLE_MAPS_API_KEY);
		return "user/publish/ride-departure";
	}

	@PostMapping("/arrival")
	public String arrival(@ModelAttribute("tripRequest") RideRequest trip, Model model) {
		model.addAttribute("GOOGLE_MAPS_API_KEY", GOOGLE_MAPS_API_KEY);
		return "user/publish/ride-arrival";
	}


	@PostMapping("/choose-your-route")
	public String chooseYourRoute(@ModelAttribute("tripRequest") RideRequest ride, Model model) {
		model.addAttribute("ride", ride);
		model.addAttribute("GOOGLE_MAPS_API_KEY", GOOGLE_MAPS_API_KEY);
		return "user/publish/chooseYourRoute";
	}


	@GetMapping("/departure-date")
	public String departureDate(@ModelAttribute("tripRequest") RideRequest trip, Model model) {
		return "user/publish/departure-date";
	}

	@GetMapping("/departure-date/time")
	public String departureDateTime(@ModelAttribute("tripRequest") RideRequest trip, Model model) {
		return "user/publish/departure-date-time";
	}

	@GetMapping("/seats")
	public String seats(@ModelAttribute("tripRequest") RideRequest trip, Model model) {
		return "user/publish/seats";
	}

	@PostMapping("/seats")
	public String saveSeats(@ModelAttribute("tripRequest") RideRequest trip, @RequestParam int seats) {
		trip.setSeats(seats);
		return "redirect:/ride/price-recommendation";
	}

	@GetMapping("/approval")
	public String approval(@ModelAttribute("tripRequest") RideRequest trip, Model model) {
		model.addAttribute("trip", trip);
		return "user/publish/approval"; // trip creation form
	}

	@GetMapping("/price-recommendation")
	public String priceRecommendation(@ModelAttribute("tripRequest") RideRequest trip, Model model) {

		double basePrice  = 0.0;
		if (trip.getVehicleType() != null) {
			VehicleFareType type = VehicleFareType.valueOf(trip.getVehicleType().toUpperCase());
			basePrice = fareCalculatorService.calculateFare(trip.getDistance(), trip.getDuration(), type);
		} else {
			basePrice = FareCalculator.calculateFare(trip.getDistance(), trip.getDuration(), 1.0);
		}

		int recommendedMin = (int) (basePrice * 0.9);
	    int recommendedMax = (int) (basePrice * 1.1);

	    model.addAttribute("basePrice", basePrice);
	    model.addAttribute("recommendedMin", recommendedMin);
	    model.addAttribute("recommendedMax", recommendedMax);
	    
		log.info("Estimated Fare: $" + basePrice);
		trip.setPrice(basePrice);
		return "user/publish/price-recommendation";
	}

	@GetMapping("/return-trip")
	public String returnTrip(@ModelAttribute("tripRequest") RideRequest trip, Model model) {
		return "user/publish/return-trip";
	}

	@GetMapping("/comment")
	public String comment(@ModelAttribute("tripRequest") RideRequest trip, Model model) {
		return "user/publish/comment";
	}

	@GetMapping("/verify-id")
	public String verify(@ModelAttribute("tripRequest") RideRequest trip, Model model) {
		return "user/publish/verify";
	}

	@PostMapping("/confirm")
    public String confirmTrip(@ModelAttribute("tripRequest") RideRequest request, Principal principal) {
    	String username = principal.getName();
		User user = driverProfileService.findByUsername(username).orElse(null);
		
		Rides ride = ridesServices.createRide(request, user);
		
		return "redirect:/user/ride/success/"+ ride.getId();
    }

	@GetMapping("/success/{tripId}")
	public String success(@PathVariable String tripId, Model model) {
		
		 model.addAttribute("tripId", tripId);
		return "user/publish/success";
	}

}