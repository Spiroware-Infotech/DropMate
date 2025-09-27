package com.dropmate.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dropmate.aspects.TrackExecutionTime;
import com.dropmate.config.CustomSuccessHandler;
import com.dropmate.entity.User;
import com.dropmate.repository.UserRepository;
import com.dropmate.service.OtpService;

import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class OtpController {

	private final UserRepository userRepo;
	private final OtpService otpService;
	private final UserDetailsService userDetailsService;
	private final CustomSuccessHandler customSuccessHandler;

	public OtpController(UserRepository userRepo, OtpService otpService, AuthenticationManager authenticationManager,
			UserDetailsService userDetailsService, CustomSuccessHandler customSuccessHandler) {
		this.userRepo = userRepo;
		this.otpService = otpService;
		this.userDetailsService = userDetailsService;
		this.customSuccessHandler = customSuccessHandler;
	}

	@TrackExecutionTime
	@GetMapping("/otp")
	public String otpPage(HttpSession session, Model model) {
		Object username = session.getAttribute("OTP_USER");
		if (username == null) {
			return "redirect:/login";
		}
		model.addAttribute("username", username.toString());
		return "auth/confirmOTP";
	}

	@TrackExecutionTime
	@PostMapping("/otp")
	public void verifyOtp(@RequestParam String otp, HttpSession session, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		String username = (String) session.getAttribute("OTP_USER");
		Long expiryTime = (Long) session.getAttribute("OTP_EXPIRY");

		// If no OTP in session or expired
		if (username == null || expiryTime == null || System.currentTimeMillis() > expiryTime) {
			session.invalidate(); // clear everything
			response.sendRedirect("/login?otpExpired");
			return;
		}

		User user = userRepo.findByUsername(username).orElse(null);
		if (user == null) {
			response.sendRedirect("/login?error");
			return;
		}

		boolean ok = otpService.validateOtp(user, otp);
		if (!ok) {
			response.sendRedirect("/otp?error");
			return;
		}

		// OTP OK → create fully authenticated Authentication
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authToken);

		// Cleanup OTP session info
		session.removeAttribute("OTP_USER");
		session.removeAttribute("OTP_EXPIRY"); // no need to keep expiry after success
		session.setAttribute("OTP_VERIFIED", true);

		// Delegate to CustomSuccessHandler → this will redirect based on role
		customSuccessHandler.onAuthenticationSuccess(request, response, authToken);
	}

	@PostMapping("/otp/resend")
	public String resendOtp(HttpSession session) {
		String username = (String) session.getAttribute("OTP_USER");
		if (username == null)
			return "redirect:/login";

		User user = userRepo.findByUsername(username).orElse(null);
		if (user != null) {
			try {
				otpService.generateAndSendOtp(user, false);
			} catch (UnsupportedEncodingException | MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "redirect:/otp?resent";
	}
}
