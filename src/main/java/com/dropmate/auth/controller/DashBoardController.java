package com.dropmate.auth.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dropmate.dto.RideRequest;

@Controller
@RequestMapping("/user")
public class DashBoardController {

	private static final Logger logger = LoggerFactory.getLogger(DashBoardController.class);
	
	
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
	public String profile(Model model) {
		model.addAttribute("activePage","profile");
		return "user/profile";
	}
}
