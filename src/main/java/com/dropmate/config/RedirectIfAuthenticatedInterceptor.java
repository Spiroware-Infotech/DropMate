package com.dropmate.config;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RedirectIfAuthenticatedInterceptor implements HandlerInterceptor {

    private static final List<String> PUBLIC_PAGES = List.of("/login", "/register");
    private static final List<String> SECURE_PAGES = List.of("/ask_questions", "/save");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String path = request.getServletPath();
        if (PUBLIC_PAGES.contains(path) && request.getUserPrincipal() != null) {
            response.sendRedirect("/user/dashboard");
            return false;
        }
        
        if (SECURE_PAGES.contains(path) && request.getUserPrincipal() == null) {
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }
}