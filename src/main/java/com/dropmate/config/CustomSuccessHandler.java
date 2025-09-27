package com.dropmate.config;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.dropmate.entity.User;
import com.dropmate.repository.UserRepository;
import com.dropmate.service.OtpService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private UserRepository userRepo; // your JPA repository
    
    @Autowired
    private OtpService otpService;

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response,
                          Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, authentication);

        if (response.isCommitted()) {
            System.out.println("Can't redirect");
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    /**
     * Decide where to go after login.
     */
    protected String determineTargetUrl(HttpServletRequest request, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepo.findByUsername(username).orElseThrow();

        HttpSession session = request.getSession(true);

        // Check if OTP verification is pending
        if (session.getAttribute("OTP_VERIFIED") == null) {
            // 1. Clear authentication from context → block access until OTP
            SecurityContextHolder.clearContext();

            // 2. Generate and send OTP
            try {
				otpService.generateAndSendOtp(user, false);
			} catch (UnsupportedEncodingException | MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            // 3. Store username in session to check later
            session.setAttribute("OTP_USER", user.getUsername());
            session.setAttribute("OTP_EXPIRY", System.currentTimeMillis() + (5 * 60 * 1000)); // 5 mins
            
            // Redirect to OTP page
            return "/otp";
        }

        // OTP already verified → proceed with role-based redirects
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<>();

        for (GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }

        if (isDba(roles)) {
            return "/db";
        } else if (isAdmin(roles)) {
            return "/admin/dashboard";
        } else if (isUser(roles)) {
            return "/user/dashboard";
        } else {
            return "/accessDenied";
        }
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    private boolean isUser(List<String> roles) {
        return roles.contains("ROLE_PASSENGER");
    }

    private boolean isAdmin(List<String> roles) {
        return roles.contains("ROLE_ADMIN");
    }

    private boolean isDba(List<String> roles) {
        return roles.contains("ROLE_DBA");
    }
}