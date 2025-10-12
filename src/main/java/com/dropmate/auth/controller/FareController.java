package com.dropmate.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dropmate.dto.FareRequest;
import com.dropmate.dto.FareResponse;
import com.dropmate.enums.VehicleFareType;
import com.dropmate.service.FareCalculatorService;

@RestController
@RequestMapping("/api/fare")
public class FareController {

    private final FareCalculatorService fareCalculatorService;

    public FareController(FareCalculatorService fareCalculatorService) {
        this.fareCalculatorService = fareCalculatorService;
    }
    
    @GetMapping("/calculate")
    public FareResponse calculateFare(@RequestBody FareRequest request) {
        VehicleFareType type = VehicleFareType.valueOf(request.getVehicleType().toUpperCase());
        double fare = fareCalculatorService.calculateFare(request.getDistance(), request.getDuration(), type);

        FareResponse response = new FareResponse();
        response.setVehicleType(type.name());
        response.setTotalFare(fare);
        response.setCurrency("INR");
        return response;
    }
}
