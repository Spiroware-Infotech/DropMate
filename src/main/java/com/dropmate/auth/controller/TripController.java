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

import com.dropmate.dto.TripRequest;
import com.dropmate.dto.TripResponse;
import com.dropmate.entity.Trip;
import com.dropmate.entity.User;
import com.dropmate.service.DriverProfileService;
import com.dropmate.service.TripService;
import com.dropmate.service.UserService;
import com.dropmate.utils.RouteUtils;
import com.dropmate.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user/ride")
@RequiredArgsConstructor
public class TripController {

	private final TripService tripService;
	
	private final DriverProfileService driverProfileService;


	// 1. Show all trips
	@GetMapping("/list")
	public String listTrips(Model model , Principal principal) {

		User user = driverProfileService.findByUsername(principal.getName()).orElse(null);
		
		List<TripResponse> trips = tripService.findByDriverId(user.getUserId());
	    Map<String, Object> groupedTrips = tripService.groupTripsByDate(trips);
	    model.addAttribute("tripsByCategory", groupedTrips);
		model.addAttribute("activePage","ridelist");
		return "/user/trip/mytrips-list";
	}

	@GetMapping("/filter")
	public String filterTrips(@RequestParam String filter, Model model) {
	    List<TripResponse> trips;

	    switch (filter.toLowerCase()) {
		    case "pending":
	            trips = tripService.getTripsByStatus("PENDING");
	            break;
		    case "scheduled":
	            trips = tripService.getTripsByStatus("SCHEDULED");
	            break;
	        case "completed":
	            trips = tripService.getTripsByStatus("COMPLETED");
	            break;
	        case "ongoing":
	            trips = tripService.getTripsByStatus("ONGOING");
	            break;
	        case "cancelled":
	            trips = tripService.getTripsByStatus("CANCELLED");
	            break;
	        default:
	            trips = tripService.getAllTrips();
	            break;
	    }

	    // Group by date (Today, Tomorrow, Older, etc.)
	    Map<String, Object> groupedTrips = tripService.groupTripsByDate(trips);

	    model.addAttribute("tripsByCategory", groupedTrips);
	    return "/user/trip/trip-list :: tripListFragment";
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
		//TripResponse trip = tripService.getTripById(tripId);
		//model.addAttribute("tripRequest", trip);
		return "trip/edit"; // trip/edit.html
	}

	// 5. Handle trip update
	@PostMapping("/{tripId}/edit")
	public String updateTrip(@PathVariable Long tripId, @ModelAttribute TripRequest tripRequest) {
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
	@GetMapping("/details/{tripId}")
	public String getTrip(@PathVariable String tripId, Model model) {
		Trip trip = tripService.getTripById(tripId);
		model.addAttribute("trip", trip);
		model.addAttribute("tripDuration", RouteUtils.formatHoursAndMinutes(trip.getDuration()));
		model.addAttribute("tripArrivalTime", TimeUtils.calculateArrivalTime(String.valueOf(trip.getStartTime()),trip.getDuration()));
		model.addAttribute("isKycVerified", 
			    trip != null && trip.getDriver() != null && "VERIFIED".equals(trip.getDriver().getKycStatus()));
		return "user/trip/ride-details"; // trip/detail.html
	}
	
	@GetMapping("/offer")
	public String getTripOffer(@RequestParam String id, Model model) {
		Trip trip = tripService.getTripById(id);
		model.addAttribute("trip", trip);
		model.addAttribute("tripDuration", RouteUtils.formatHoursAndMinutes(trip.getDuration()));
		model.addAttribute("tripArrivalTime", TimeUtils.calculateArrivalTime(String.valueOf(trip.getStartTime()),trip.getDuration()));
		model.addAttribute("isKycVerified", 
			    trip != null && trip.getDriver() != null && "VERIFIED".equals(trip.getDriver().getKycStatus()));
		return "user/trip/ride-plan"; // trip/detail.html
	}

	// 8. Search trips (simple form -> results)
	@GetMapping("/search")
	public String searchTrips(@RequestParam String from, @RequestParam String to,
			@RequestParam(required = false) String date, Model model) {
		List<TripResponse> trips = tripService.searchTrips(from, to, date);
		model.addAttribute("trips", trips);
		return "trip/search-results"; // trip/search-results.html
	}
}
