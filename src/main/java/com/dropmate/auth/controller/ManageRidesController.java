package com.dropmate.auth.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dropmate.dto.TripBookingDTO;
import com.dropmate.dto.TripResponse;
import com.dropmate.entity.Booking;
import com.dropmate.entity.DriverProfile;
import com.dropmate.entity.Rides;
import com.dropmate.entity.User;
import com.dropmate.service.BookingService;
import com.dropmate.service.DriverProfileService;
import com.dropmate.service.RidesServices;
import com.dropmate.utils.RouteUtils;
import com.dropmate.utils.TimeUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/user/ride")
@RequiredArgsConstructor
public class ManageRidesController {
	
	private final RidesServices ridesServices;
	private final DriverProfileService driverProfileService;
	private final BookingService bookingService;

	
	// 1. Show all trips
	@GetMapping("/history")
	public String listRidesHistory(Model model , Principal principal) {

		User user = driverProfileService.findByUsername(principal.getName()).orElse(null);
		
		List<TripResponse> trips = ridesServices.getAllTrips(user.getUserId());
	    Map<String, Object> groupedTrips = ridesServices.groupTripsByDate(trips);
	    model.addAttribute("tripsByCategory", groupedTrips);
		model.addAttribute("activePage","ridelist");
		return "/user/rides/myrides-history";
	}
	
	@GetMapping("/booking")
	public String listTrips(Model model , Principal principal) {

		User user = driverProfileService.findByUsername(principal.getName()).orElse(null);
		
		List<TripResponse> trips = ridesServices.findRidesBookedByUser(user.getUserId());
	    Map<String, Object> groupedTrips = ridesServices.groupTripsByDate(trips);
	    model.addAttribute("tripsByCategory", groupedTrips);
		model.addAttribute("activePage","bookinglist");
		return "/user/rides/booking-rides";
	}

	@GetMapping("/filter")
	public String filterTrips(@RequestParam String filter, Model model, Principal principal) {
		User user = driverProfileService.findByUsername(principal.getName()).orElse(null);
	    List<TripResponse> trips;

	    switch (filter.toLowerCase()) {
		    case "pending":
	            trips = ridesServices.getTripsByStatus("PENDING",user.getUserId());
	            break;
		    case "scheduled":
	            trips = ridesServices.getTripsByStatus("SCHEDULED", user.getUserId());
	            break;
	        case "completed":
	            trips = ridesServices.getTripsByStatus("COMPLETED", user.getUserId());
	            break;
	        case "ongoing":
	            trips = ridesServices.getTripsByStatus("ONGOING", user.getUserId());
	            break;
	        case "cancelled":
	            trips = ridesServices.getTripsByStatus("CANCELLED", user.getUserId());
	            break;
	        default:
	            trips = ridesServices.getAllTrips(user.getUserId());
	            break;
	    }

	    // Group by date (Today, Tomorrow, Older, etc.)
	    Map<String, Object> groupedTrips = ridesServices.groupTripsByDate(trips);

	    model.addAttribute("tripsByCategory", groupedTrips);
	    return "/user/rides/rides-list :: tripListFragment";
	}
		
	// 1. Ride details by ID
	@GetMapping("/details/{tripId}")
	public String getTrip(@PathVariable String tripId, Model model, Principal principal) {
		Rides ride = ridesServices.getRideById(tripId);
		User currentUser = driverProfileService.findByUsername(principal.getName()).orElse(null);
		model.addAttribute("ride", ride);
		model.addAttribute("tripDuration", RouteUtils.formatHoursAndMinutes(ride.getDurationInSec()));
		model.addAttribute("tripArrivalTime", TimeUtils.calculateArrivalTime(String.valueOf(ride.getStartTime()),ride.getDurationInSec()));
//		model.addAttribute("isKycVerified", 
//				ride != null && ride.getDriver() != null && "VERIFIED".equals(ride.getDriver().getKycStatus()));
		 model.addAttribute("currentUser", currentUser);
		return "user/rides/ride-details"; // trip/detail.html
	}
	
	
	@GetMapping("/offer")
	public String getTripOffer(@RequestParam String id, Model model) {
		Rides ride = ridesServices.getRideById(id);
		model.addAttribute("ride", ride);
		model.addAttribute("tripDuration", RouteUtils.formatHoursAndMinutes(ride.getDurationInSec()));
		model.addAttribute("tripArrivalTime", TimeUtils.calculateArrivalTime(String.valueOf(ride.getStartTime()),ride.getDurationInSec()));
//		model.addAttribute("isKycVerified", 
//				ride != null && ride.getDriver() != null && "VERIFIED".equals(ride.getDriver().getKycStatus()));
		return "user/rides/ride-plan"; 
	}
	

	@GetMapping("/booking/checkout")
	public String booking(@RequestParam String id, Model model) {
		Rides ride = ridesServices.getRideById(id);
		model.addAttribute("ride", ride);
		model.addAttribute("tripDuration", RouteUtils.formatHoursAndMinutes(ride.getDurationInSec()));
		model.addAttribute("tripArrivalTime", TimeUtils.calculateArrivalTime(String.valueOf(ride.getStartTime()),ride.getDurationInSec()));
//		model.addAttribute("isKycVerified", 
//				ride != null && ride.getDriver() != null && "VERIFIED".equals(ride.getDriver().getKycStatus()));
		model.addAttribute("tripId", id);
		return "user/rides/ride-booking"; 
	}
	
	

	@PostMapping("/booking")
	public String bookingSubmit(@ModelAttribute TripBookingDTO bookingDTO, Model model, Principal principal) {
		log.info("Ride Booking Submit....");
		User user = driverProfileService.findByUsername(principal.getName()).orElse(null);
		
		Booking booking = bookingService.createPassengerBooking(bookingDTO.getTripId(), user.getUserId(), 1);
		
		return "redirect:/user/ride/booking-success/"+booking.getId(); 
	}
	
	
	@GetMapping("/booking-success/{bookingId}")
	public String success(@PathVariable String bookingId, Model model) {
		 Booking booking = bookingService.getBookingById(bookingId);
		 DriverProfile driver = driverProfileService.getDriverProfileById(booking.getPassenger().getUserId());
		 model.addAttribute("booking", booking);
		 model.addAttribute("driver", driver);
		return "user/trip/booking-success";
	}
	
	
}
