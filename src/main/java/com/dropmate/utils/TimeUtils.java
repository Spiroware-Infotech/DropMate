package com.dropmate.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

	public static String calculateArrivalTime(String startTime, long durationSeconds) {
		// Define input format (e.g. "HH:mm")
		DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("HH:mm");

		// Parse the starting time
		LocalTime start = LocalTime.parse(startTime, inputFormat);

		// Add duration (in seconds)
		LocalTime arrival = start.plusSeconds(durationSeconds);

		// Format output (you can change this to "hh:mm a" if you want AM/PM)
		DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("HH:mm");

		return arrival.format(outputFormat);
	}

	public static String calculateEndTime(String startTimeStr, long durationInSeconds) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		LocalTime startTime = LocalTime.parse(startTimeStr, formatter);
		LocalTime endTime = startTime.plusSeconds(durationInSeconds);
		return endTime.format(formatter);
	}

}
