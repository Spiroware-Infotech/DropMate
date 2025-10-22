package com.dropmate.auth.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.dropmate.controller.CommonController;
import com.dropmate.dto.DeliveryRequest;
import com.dropmate.entity.DriverProfile;
import com.dropmate.entity.Trip;
import com.dropmate.entity.User;
import com.dropmate.enums.KycStatus;
import com.dropmate.enums.TripStatus;
import com.dropmate.enums.TripType;
import com.dropmate.enums.VehicleType;
import com.dropmate.service.DeliveryService;
import com.dropmate.service.DriverProfileService;
import com.dropmate.service.TripService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/user/delivery")
@SessionAttributes("deliveryRequest") // store in session across all steps
@RequiredArgsConstructor
public class ItemDeliveryController extends CommonController{

	private final TripService tripService;
	private final DeliveryService deliveryService;
	private final DriverProfileService driverProfileService;
	
	@ModelAttribute("deliveryRequest")
	public DeliveryRequest deliveryRequest(Model model) {
		return new DeliveryRequest(); // initialize once
	}
	
	// Step 2: Delivery form
	@GetMapping("/departure")
	public String showDeliveryForm(@ModelAttribute("deliveryRequest") DeliveryRequest request, Model model) {
		model.addAttribute("GOOGLE_MAPS_API_KEY", GOOGLE_MAPS_API_KEY);
		return "user/delivery/delivery-departure"; // delivery creation form
	}
	
	@PostMapping("/arrival")
	public String arrival(@ModelAttribute("deliveryRequest") DeliveryRequest request, Model model) {
		model.addAttribute("GOOGLE_MAPS_API_KEY", GOOGLE_MAPS_API_KEY);
		return "user/delivery/delivery-arrival";
	}


	@PostMapping("/choose-your-route")
	public String chooseYourRoute(@ModelAttribute("deliveryRequest") DeliveryRequest request, Model model) {
		model.addAttribute("request", request);
		model.addAttribute("GOOGLE_MAPS_API_KEY", GOOGLE_MAPS_API_KEY);
		return "user/delivery/chooseYourRoute";
	}
	
	@GetMapping("/date")
	public String departureDate(@ModelAttribute("deliveryRequest") DeliveryRequest request, Model model) {
		return "user/delivery/delivery-date";
	}
	
	@GetMapping("/vehicle")
	public String vehicle(@ModelAttribute("deliveryRequest") DeliveryRequest request, Model model) {
		return "user/delivery/delivery-vehicle";
	}
	
	// Optional: Confirm page for Delivery
	@PostMapping("/confirm")
	public String confirmDelivery(@ModelAttribute("deliveryRequest") DeliveryRequest request, Model model,Principal principal) {
		
		try {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    	LocalDate startDate = LocalDate.parse(request.getStartTime(), formatter);
    	
    	String username = principal.getName();
    	User user = driverProfileService.findByUsername(username).orElse(null);
    	
    	DriverProfile driverProfile = DriverProfile.builder()
    			.id(user.getUserId())
    			.kycStatus(KycStatus.PENDING)
    			.vehicleType(VehicleType.valueOf(request.getVehicleType().toUpperCase()))
    			.build();
    	
    	driverProfileService.saveDriverProfile(driverProfile);
    	
    	TripStatus rideStatus= null;
    	if(request.getReturnRideOption()!=null) {
    		if(request.getReturnRideOption().equalsIgnoreCase("yes"))
    			rideStatus = TripStatus.COMPLETED;
    		else if(request.getReturnRideOption().equalsIgnoreCase("later"))
    			rideStatus = TripStatus.SCHEDULED;
    	}
    	ObjectMapper mapper = new ObjectMapper();
    	 // Save to DB here
    	Trip trip = Trip.builder()
			.originName(request.getOriginName())
			.destinationName(request.getDestinationName())
			.tripType(TripType.DELIVERY)
			.status(rideStatus)
			.startDate(startDate)
			//.pricePerSeat(new BigDecimal(request.getPrice()))
			.vehicleType(VehicleType.valueOf(request.getVehicleType().toUpperCase()))
			.bookingType("INSTANT")
			.duration(request.getDuration())
			.distance(request.getDistance())
			.source(mapper.writeValueAsString(request.getSourceJson()))
			.destination(mapper.writeValueAsString(request.getDestinationJson()))
			.build();
			
    	Trip tripDB = tripService.createTrip(user.getUserId(), trip, new ArrayList());
    	
    	return "redirect:/user/delivery/delivery-sucess/"+ tripDB.getId(); // confirmation page
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return null; // confirmation page
	}
	
	@GetMapping("/delivery-sucess/{tripId}")
	public String success(@PathVariable String tripId, Model model) {
		
		 model.addAttribute("tripId", tripId);
		 model.addAttribute("GOOGLE_MAPS_API_KEY", GOOGLE_MAPS_API_KEY);
		return "user/delivery/delivery-sucess";
	}
}
