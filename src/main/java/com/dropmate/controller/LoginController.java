package com.dropmate.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String getLogin(Model model, Authentication authentication) {
        model.addAttribute("loginAct","current_page_item");
        return "auth/login";
    }
    
   
    
    @GetMapping("/forgotpwd")
    public String forgotpwd(Model model, Authentication authentication) {
        model.addAttribute("forPwdAct","current_page_item");
        return "auth/forgotpwd";
    }
    
}
