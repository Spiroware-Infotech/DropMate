package com.dropmate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.dropmate.aspects.TrackExecutionTime;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class HomeController extends CommonController {

	@TrackExecutionTime
	@GetMapping("/")
	public String index(Model model, HttpServletRequest request) {
		log.info("HomeController --> index ----> START");
		model.addAttribute("GOOGLE_MAPS_API_KEY", GOOGLE_MAPS_API_KEY);
		model.addAttribute("indexAct","current_page_item");
		return "index";
	}

	
}
