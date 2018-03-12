package org.aatm;

import java.util.Arrays;

public enum DayOfWeek {

	MON, TUE, WED, THU, FRI, SAT, SUN;

	public static boolean exists(String valueToTest) {
		return Arrays.asList(DayOfWeek.values())
				.stream()
				.anyMatch(day -> day.name().equalsIgnoreCase(valueToTest));
	}
}
