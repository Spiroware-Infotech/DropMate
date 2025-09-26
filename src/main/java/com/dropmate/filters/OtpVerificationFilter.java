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
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated()) {
            HttpSession session = request.getSession(false);
            boolean otpVerified = session != null && Boolean.TRUE.equals(session.getAttribute("OTP_VERIFIED"));
            
            String path = request.getRequestURI();
            
            if (!otpVerified && path.startsWith("/user/")) {
                response.sendRedirect("/otp");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
