package com.dropmate.config;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.dropmate.entity.User;
import com.dropmate.repository.UserRepository;
import com.dropmate.service.OtpService;

import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginSuccessOtpHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepo;
    private final OtpService otpService;

    public LoginSuccessOtpHandler(UserRepository userRepo, OtpService otpService) {
        this.userRepo = userRepo;
        this.otpService = otpService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        User user = userRepo.findByUsername(username).orElseThrow();
        // IMPORTANT: invalidate the initial security context so the session is not fully authenticated yet
        SecurityContextHolder.clearContext();

        // Generate and send OTP
        try {
			otpService.generateAndSendOtp(user, /*sendSms*/ false);
		} catch (UnsupportedEncodingException | MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Save temporary username in session for OTP verification
        request.getSession(true).setAttribute("OTP_USER", user.getUsername());

        // Redirect to OTP page
        response.sendRedirect("/otp");
    }
}
