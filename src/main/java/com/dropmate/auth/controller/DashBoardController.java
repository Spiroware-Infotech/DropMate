package com.dropmate.auth.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dropmate.entity.DriverProfile;
import com.dropmate.entity.Rating;
import com.dropmate.entity.User;
import com.dropmate.service.DriverProfileService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class DashBoardController {

	private static final Logger logger = LoggerFactory.getLogger(DashBoardController.class);
	
	private final DriverProfileService driverProfileService;
	
//	@Autowired
//	PaymentService paymentService;
	
	@GetMapping("/dashboard")
	public String dashboard(Model model, Principal principal,Authentication authentication) {
		logger.info("Welcome to dashboard!..");
		 if (authentication == null) {
	            // Redirect to home if logged in already
	            return "redirect:/login";
	        }
//		PaymentCalculations paymentStatastics= paymentService.getPaymentStasticts();
//		
		model.addAttribute("activePage","dashboard");
		return "user/dashboard";
	}
	
	@GetMapping(value = "/profile")
	public String profile(Model model , Principal principal) {
		User user = driverProfileService.findByUsername(principal.getName()).orElse(null);
		DriverProfile driver = driverProfileService.findById(user.getUserId());
		model.addAttribute("user",user);
		model.addAttribute("driver",driver);
		model.addAttribute("activePage","profile");
		return "user/profile";
	}
	
//	@PostMapping("/rate")
//	public String submitRating(@ModelAttribute RatingDto dto, Principal principal) {
//	    User ratedBy = userService.getByEmail(principal.getName());
//	    User ratedTo = userService.getById(dto.getRatedToId());
//	    Rides ride = rideService.getById(dto.getRideId());
//
//	    Rating rating = new Rating(ratedBy, ratedTo, ride, dto.getScore(), dto.getComment());
//	    ratingRepository.save(rating);
//
//	    experienceService.updateUserExperience(ratedTo);
//
//	    return "redirect:/rides/history";
//	}
}
