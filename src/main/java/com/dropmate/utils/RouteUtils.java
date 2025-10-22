package com.dropmate.utils;

public class RouteUtils {

	// Distance conversion
	public static double metersToKilometers(double meters) {
		return Math.round((meters / 1000) * 100.0) / 100.0; // 2 decimal places
	}

	public static double metersToMiles(double meters) {
		return Math.round((meters / 1609.34) * 100.0) / 100.0; // 2 decimal places
	}

	// Time conversion
	public static double millisecondsToHours(long milliseconds) {
		return Math.round((milliseconds / (1000.0 * 60 * 60)) * 100.0) / 100.0; // 2 decimal places
	}

	public static double millisecondsToMinutes(long milliseconds) {
		return Math.round((milliseconds / (1000.0 * 60)) * 100.0) / 100.0; // 2 decimal places
	}

	public static String formatDuration(long milliseconds) {
		long seconds = milliseconds / 1000;
		long hours = seconds / 3600;
		long minutes = (seconds % 3600) / 60;

		if (hours > 0) {
			return String.format("%d hr %d min", hours, minutes);
		} else {
			return String.format("%d min", minutes);
		}
	}

	public static String formatHoursAndMinutes(long seconds) {
		long hours = seconds / 3600;
		long minutes = (seconds % 3600) / 60;

		if (hours > 0 && minutes > 0) {
			return hours + "hr " + minutes + " min";
		} else if (hours > 0) {
			return hours + "hr";
		} else {
			return minutes + " min";
		}
	}
	
	public static String formatHoursAndMinutes(long secondsOrMillis, boolean isMillis) {
	    long seconds = isMillis ? secondsOrMillis / 1000 : secondsOrMillis;

	    long hours = seconds / 3600;
	    long minutes = (seconds % 3600) / 60;

	    if (hours > 0 && minutes > 0) {
	        return hours + "hr " + minutes + " min";
	    } else if (hours > 0) {
	        return hours + "hr";
	    } else {
	        return minutes + " min";
	    }
	}

	public static String formatDistance(double meters) {
		if (meters < 1000) {
			return String.format("%.0f m", meters);
		} else {
			double km = metersToKilometers(meters);
			return String.format("%.1f km", km);
		}
	}

	// Complete route information formatter
	public static String formatRouteInfo(double distanceMeters, long timeMilliseconds) {
		String distance = formatDistance(distanceMeters);
		String duration = formatDuration(timeMilliseconds);
		return String.format("%s • %s", distance, duration);
	}

	// Calculate average speed in km/h
	public static double calculateAverageSpeed(double distanceMeters, long timeMilliseconds) {
		if (timeMilliseconds == 0)
			return 0;
		double hours = millisecondsToHours(timeMilliseconds);
		double distanceKm = metersToKilometers(distanceMeters);
		return Math.round((distanceKm / hours) * 100.0) / 100.0;
	}
	
	
}