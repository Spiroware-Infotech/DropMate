package com.organization.api;

import com.dropmate.dto.PlaceInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestingMain {

	public static void main(String[] args) throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "{\"name\":\"Gandipalem\",\"address\":\"Gandipalem, Andhra Pradesh 524236, India\",\"latitude\":14.9986191,\"longitude\":79.3095848}";

		PlaceInfo src = mapper.readValue(jsonString, PlaceInfo.class);

		if (src != null) {
		    System.out.println("Latitude: " + src.getLatitude());
		    System.out.println("Longitude: " + src.getLongitude());
		} else {
		    System.out.println("Source JSON conversion failed!");
		}

	}

}
