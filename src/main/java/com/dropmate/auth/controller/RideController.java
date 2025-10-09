package com.dropmate.auth.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.dropmate.dto.RideRequest;
import com.dropmate.entity.DriverProfile;
import com.dropmate.entity.Trip;
import com.dropmate.entity.User;
import com.dropmate.enums.KycStatus;
import com.dropmate.enums.TripStatus;
import com.dropmate.enums.TripType;
import com.dropmate.enums.VehicleFareType;
import com.dropmate.enums.VehicleType;
import com.dropmate.service.DriverProfileService;
import com.dropmate.service.FareCalculatorService;
import com.dropmate.service.TripService;
import com.dropmate.utils.FareCalculator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/user/ride")
@SessionAttributes("tripRequest") // store in session across all steps
public class RideController {

	@Autowired
	private FareCalculatorService fareCalculatorService;

	@Autowired
	DriverProfileService driverProfileService;

	@Autowired
	TripService tripService;

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
		return "user/publish/ride-departure";
	}


	@GetMapping("/arrival")
	public String arrival(@ModelAttribute("tripRequest") RideRequest trip, Model model) {
		return "user/publish/ride-arrival";
	}


	@GetMapping("/choose-your-route")
	public String chooseYourRoute(@ModelAttribute("tripRequest") RideRequest ride, Model model) {
		model.addAttribute("ride", ride);
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
		boolean instantBookingEnabled = false; // Default value
		model.addAttribute("instantBookingEnabled", instantBookingEnabled);
		return "user/publish/approval"; // trip creation form
	}

	@GetMapping("/price-recommendation")
	public String priceRecommendation(@ModelAttribute("tripRequest") RideRequest trip, Model model) {

		double fare = 0.0;
		if (trip.getVehicleType() != null) {
			VehicleFareType type = VehicleFareType.valueOf(trip.getVehicleType().toUpperCase());

			fare = fareCalculatorService.calculateFare(trip.getDistance(), trip.getDuration(), type);
		} else {
			fare = FareCalculator.calculateFare(trip.getDistance(), trip.getDuration(), 1.0);
		}

		log.info("Estimated Fare: $" + fare);
		trip.setPrice(fare);
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

//    @GetMapping("/create-return-ride")
//    public String createReturnRide(@ModelAttribute("tripRequest") RideRequest trip, Model model, Principal principal) {
//    	 String username = principal.getName();
//    	 User user = driverProfileService.findByUsername(username).orElse(null);
//    	 if(Objects.nonNull(user)) {
//    		 DriverProfile driverProfile = driverProfileService.getDriverProfileById(user.getUserId());
//    		 if(Objects.isNull(driverProfile)) {
//    			 return "redirect:/user/ride/verify-id";
//    		 }
//    	 }
//        return "user/publish/create-return-ride";
//    }

	@GetMapping("/verify-id")
	public String verify(@ModelAttribute("tripRequest") RideRequest trip, Model model) {
		return "user/publish/verify";
	}

	@PostMapping("/confirm")
    public String confirmTrip(@ModelAttribute("tripRequest") RideRequest request, Principal principal) {
        // Save to DB here
    	log.info("Final trip data: " + request);
    	
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    	LocalDate startDate = LocalDate.parse(request.getDepartureDate(), formatter);
    	
    	LocalTime time = LocalTime.parse(request.getDepartureTime(), DateTimeFormatter.ofPattern("HH:mm"));
    	
    	String username = principal.getName();
    	User user = driverProfileService.findByUsername(username).orElse(null);
    	
    	DriverProfile driverProfile = DriverProfile.builder()
    			.userId(user.getUserId())
    			.kycStatus(KycStatus.PENDING)
    			//.vehicleType(VehicleType.valueOf(request.getVehicleType()))
    			.vehicleType(VehicleType.CAR)
    			.build();
    	
    	driverProfileService.saveDriverProfile(driverProfile);
    	
    	Trip trip = Trip.builder()
    			.originName(request.getDeparture())
    			.destinationName(request.getArrival())
    			.seatsAvailable(request.getSeats())
    			.seatsTotal(request.getSeats())
    			.tripType(TripType.PASSENGER)
    			.status(TripStatus.SCHEDULED)
    			.startDate(startDate)
    			.startTime(time)
    			.pricePerSeat(new BigDecimal(request.getPrice()))
    			//.vehicleType(VehicleType.valueOf(request.getVehicleType()))
    			.vehicleType(VehicleType.CAR)
    			.build();
    	
    	tripService.createTrip(user.getUserId(), trip, new ArrayList());
    	
        return "redirect:/user/ride/success";
    }

	@GetMapping("/success")
	public String success() {
		return "user/publish/success";
	}

}