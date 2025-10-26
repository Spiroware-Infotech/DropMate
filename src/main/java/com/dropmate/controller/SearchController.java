package com.dropmate.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.dropmate.dto.TripSearchRequest;
import com.dropmate.entity.Trip;
import com.dropmate.search.RidesResponse;
import com.dropmate.service.SearchRidesService;
import com.dropmate.service.TripService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SearchController extends CommonController {

	private final TripService tripService;
	private final SearchRidesService searchRidesService;

//	@GetMapping("/search")
//    public String searchResults(Model model, Authentication authentication) {
//        model.addAttribute("loginAct","current_page_item");
//        return "searchResults";
//    }

	@GetMapping("/search")
	public String searchTrips(@RequestParam double sourceLat, @RequestParam double sourceLng,
			@RequestParam double destLat, @RequestParam double destLng,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestParam Integer seats,
			Model model) {

		List<Trip> trips = tripService.searchRides(sourceLat, sourceLng, destLat, destLng, date);
		model.addAttribute("trips", trips);
		model.addAttribute("date", date);
		return "searchResults";
	}

	@GetMapping("/rides/search")
	public String searchTripsByAddress(@ModelAttribute TripSearchRequest req, 
			Model model) {
		
		List<RidesResponse> trips = searchRidesService.searchTripsBySourceAndDestination(req);
		model.addAttribute("trips", trips);
		model.addAttribute("req", req);
		
		return "searchResults";

	}

}
