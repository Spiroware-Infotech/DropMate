package com.dropmate.utils;

import java.time.LocalDate;
import java.time.Period;

public class AgeUtil {

	public static String calculateAge(LocalDate dateOfBirth) {
		if (dateOfBirth == null)
			return "N/A";

		LocalDate today = LocalDate.now();
		int years = Period.between(dateOfBirth, today).getYears();

		return years + " y/o";
	}

}
