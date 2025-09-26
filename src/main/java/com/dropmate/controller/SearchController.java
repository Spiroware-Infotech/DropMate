package com.dropmate.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

	@GetMapping("/search")
    public String searchResults(Model model, Authentication authentication) {
        model.addAttribute("loginAct","current_page_item");
        return "searchResults";
    }
}
