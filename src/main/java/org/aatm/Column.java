package org.aatm;

import java.util.Map;

public enum Column {

	DAY("Day"),
	HIJRI_MONTH("Hijri month", "hijriMonth"),
	MONTH("Month", "gregorianMonth"),
	FAJR_START("Fajr Start"),
	FAJR_JAMAAH("Fajr Jamaa'ah"),
	SUNRISE("Sunrise"),
	DHUHR_START("Dhuhr Start"),
	DHUHR_JAMAAH("Dhuhr Jamaa'ah"),
	ASR_START("'Asr Start"),
	ASR_JAMAAH("'Asr Jamaa'ah"),
	MAGHRIB_JAMAAH("Maghrib Jamaa'ah"),
	ISHAA_START("'Ishaa Start"),
	ISHAA_JAMAAH("'Ishaa Jamaa'ah");
	
	private String label;
	private String key;
	
	Column(String label) {
		this(label, null);
	}
	
	Column(String label, String key) {
		this.label = label;
		this.key = key;
	}
	
	public String getValue(Map<String, String> context) {
		if(key != null && context.containsKey(key)) {
			return context.get(key);
		} else {
			return label;
		}
	}
}
