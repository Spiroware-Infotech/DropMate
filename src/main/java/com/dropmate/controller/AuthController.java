package com.dropmate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dropmate.aspects.TrackExecutionTime;
import com.dropmate.dto.RegistrationDto;
import com.dropmate.entity.User;
import com.dropmate.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin("*")
@Controller
@Slf4j
public class AuthController {

	@Autowired
	UserService userService;

	@GetMapping("/signup")
	public String signup(Model model, Authentication authentication) {
		model.addAttribute("signupAct", "current_page_item");
		return "auth/signup";
	}

	@TrackExecutionTime
	@PostMapping("/signup")
	public String processRegistrationForm(@Valid @ModelAttribute("user") RegistrationDto user, BindingResult result,
			Model model) {
		user.setUsername(user.getEmail());
		result = registrationDuplicate(user, result);
		if (result.hasErrors()) {
			model.addAttribute("user", user);
			return "auth/signup";
		}
		userService.saveUser(user);
		return "redirect:/login?success";
	}

	@TrackExecutionTime
	@GetMapping(value = "/registration_success")
	public ModelAndView registrationSuccess(Model model) {
		log.info("registration success!..");
		ModelAndView mView = new ModelAndView("registration_success");
		String success = (String) model.asMap().get("success");
		User user = (User) model.asMap().get("user");

		mView.addObject("success", success);
		mView.addObject("user", user);
		return mView;
	}

	// verify user
	@GetMapping("/verify")
	public String verifyAcount(@RequestParam("code") String code, Model model) {
		if (userService.verify(code)) {
			return "redirect:/login";
		} else {
			model.addAttribute("verifyError", "Verification Faild!");
			return "verify_fail";
		}
	}

	private BindingResult registrationDuplicate(RegistrationDto user, BindingResult result) {
		boolean existingUser = userService.existsByUsername(user.getEmail());

		if (existingUser) {
			result.rejectValue("email", String.valueOf(402), "There is already a user with this email address.");
		}
//		existingUser = userService.findByUsername(user.getUsername());
//
//		if (existingUser != null) {
//			result.rejectValue("username", String.valueOf(402), "Username is already taken.");
//		}

		return result;
	}
}
