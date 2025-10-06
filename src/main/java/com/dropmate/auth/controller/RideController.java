package com.dropmate.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.dropmate.dto.RideRequest;
import com.dropmate.utils.FareCalculator;
import com.dropmate.utils.FareCalculator;

@Controller
@RequestMapping("/user/ride")
@SessionAttributes("tripRequest")  // store in session across all steps
public class RideController {

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

//    @PostMapping("/departure")
//    public String saveDeparture(@ModelAttribute("tripRequest") RideRequest trip, 
//                                @RequestParam String departureLocation) {
//        trip.setDeparture(departureLocation);
//        return "redirect:/user/ride/arrival";
//    }

    @GetMapping("/arrival")
    public String arrival(@ModelAttribute("tripRequest") RideRequest trip, Model model) {
        return "user/publish/ride-arrival";
    }

//    @PostMapping("/arrival")
//    public String saveArrival(@ModelAttribute("tripRequest") RideRequest trip,
//                              @RequestParam String arrivalLocation) {
//        trip.setArrival(arrivalLocation);
//        return "redirect:/user/ride/choose-your-route";
//    }

    @GetMapping("/choose-your-route")
    public String chooseYourRoute(@ModelAttribute("tripRequest") RideRequest ride, Model model) {
    	model.addAttribute("ride", ride);
        return "user/publish/chooseYourRoute";
    }

//    @PostMapping("/choose-your-route")
//    public String saveRoute(@ModelAttribute("tripRequest") RideRequest trip,
//                            @RequestParam String route) {
//        trip.setRoute(route);
//        return "redirect:/ride/departure-date";
//    }

    @GetMapping("/departure-date")
    public String departureDate(@ModelAttribute("tripRequest") RideRequest trip, Model model) {
        return "user/publish/departure-date";
    }

//    @PostMapping("/departure-date")
//    public String saveDate(@ModelAttribute("tripRequest") RideRequest trip,
//                           @RequestParam String departureDate) {
//        trip.setDepartureDate(departureDate);
//        return "redirect:/ride/departure-date/time";
//    }

    @GetMapping("/departure-date/time")
    public String departureDateTime(@ModelAttribute("tripRequest") RideRequest trip, Model model) {
        return "user/publish/departure-date-time";
    }

//    @PostMapping("/departure-date/time")
//    public String saveTime(@ModelAttribute("tripRequest") RideRequest trip,
//                           @RequestParam String departureTime) {
//        trip.setDepartureTime(departureTime);
//        return "redirect:/ride/seats";
//    }

    @GetMapping("/seats")
    public String seats(@ModelAttribute("tripRequest") RideRequest trip, Model model) {
        return "user/publish/seats";
    }

    @PostMapping("/seats")
    public String saveSeats(@ModelAttribute("tripRequest") RideRequest trip,
                            @RequestParam int seats) {
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
    	double fare = FareCalculator.calculateFare(trip.getDistance(), trip.getDuration(), 1.0);
    	System.out.println("Estimated Fare: $" + fare);
    	//trip.setPrice(fare);
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
    
    @PostMapping("/confirm")
    public String confirmTrip(@ModelAttribute("tripRequest") RideRequest trip) {
        // Save to DB here
        System.out.println("Final trip data: " + trip);
        return "redirect:/ride/success";
    }

    @GetMapping("/success")
    public String success() {
        return "user/publish/success";
    }
}