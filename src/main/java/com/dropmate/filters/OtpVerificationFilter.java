package com.dropmate.filters;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class OtpVerificationFilter extends OncePerRequestFilter {

    private static final String OTP_VERIFIED = "OTP_VERIFIED";
    private static final String OTP_EXPIRY = "OTP_EXPIRY";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        HttpSession session = request.getSession(false);
        String path = request.getRequestURI();

        // Case 1: user not authenticated -> let Spring Security handle (goes to /login)
        if (auth == null || !auth.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean otpVerified = false;
        Long expiryTime = null;

        if (session != null) {
            Object verifiedAttr = session.getAttribute(OTP_VERIFIED);
            otpVerified = (verifiedAttr instanceof Boolean) && (Boolean) verifiedAttr;

            Object expiryAttr = session.getAttribute(OTP_EXPIRY);
            expiryTime = (expiryAttr instanceof Long) ? (Long) expiryAttr : null;
        }

        // Case 2: if OTP expired -> clear session + force login
        if (expiryTime != null && System.currentTimeMillis() > expiryTime) {
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect("/login?otpExpired");
            return;
        }

        // Case 3: if OTP not verified yet -> block protected URLs
        if (!otpVerified &&
                (path.startsWith("/user/") || path.startsWith("/admin/") || path.startsWith("/db/"))) {
            response.sendRedirect("/otp");
            return;
        }

        // Case 4: everything else -> continue
        filterChain.doFilter(request, response);
    }
}

