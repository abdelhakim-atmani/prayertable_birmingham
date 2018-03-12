package org.aatm;

import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class PrayerTime {

	private DayOfWeek dayOfWeek;
	private String dayDateInHijri;
	private String dayDateInGregorian;
	private String fajrStart;
	private String fajrJam;
	private String sunrise;
	private String dhuhrStart;
	private String dhuhrJam;
	private String asrStart;
	private String asrJam;
	private String maghribJam;
	private String ishaaStart;
	private String ishaaJam;
	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	public String getDayDateInHijri() {
		return dayDateInHijri;
	}
	public void setDayDateInHijri(String dayDateInHijri) {
		this.dayDateInHijri = dayDateInHijri;
	}
	public String getDayDateInGregorian() {
		return dayDateInGregorian;
	}
	public void setDayDateInGregorian(String dayDateInGregorian) {
		this.dayDateInGregorian = dayDateInGregorian;
	}
	public String getFajrStart() {
		return fajrStart;
	}
	public void setFajrStart(String fajrStart) {
		this.fajrStart = fajrStart;
	}
	public String getFajrJam() {
		return fajrJam;
	}
	public void setFajrJam(String fajrJam) {
		this.fajrJam = fajrJam;
	}
	public String getSunrise() {
		return sunrise;
	}
	public void setSunrise(String sunrise) {
		this.sunrise = sunrise;
	}
	public String getDhuhrStart() {
		return dhuhrStart;
	}
	public void setDhuhrStart(String dhuhrStart) {
		this.dhuhrStart = dhuhrStart;
	}
	public String getDhuhrJam() {
		return dhuhrJam;
	}
	public void setDhuhrJam(String dhuhrJam) {
		this.dhuhrJam = dhuhrJam;
	}
	public String getAsrStart() {
		return asrStart;
	}
	public void setAsrStart(String asrStart) {
		this.asrStart = asrStart;
	}
	public String getAsrJam() {
		return asrJam;
	}
	public void setAsrJam(String asrJam) {
		this.asrJam = asrJam;
	}
	public String getMaghribJam() {
		return maghribJam;
	}
	public void setMaghribJam(String maghribJam) {
		this.maghribJam = maghribJam;
	}
	public String getIshaaStart() {
		return ishaaStart;
	}
	public void setIshaaStart(String ishaaStart) {
		this.ishaaStart = ishaaStart;
	}
	public String getIshaaJam() {
		return ishaaJam;
	}
	public void setIshaaJam(String ishaaJam) {
		this.ishaaJam = ishaaJam;
	}
	
	public void set(Column column, String value) {
		switch(column) {
			case DAY:
				this.setDayOfWeek(extractDayOfWeek(value).get());
				break;
			case HIJRI_MONTH:
				this.setDayDateInHijri(value);
				break;
			case MONTH:
				this.setDayDateInGregorian(value);
				break;
			case FAJR_START:
				this.setFajrStart(value);
				break;
			case FAJR_JAMAAH:
				this.setFajrJam(value);
				break;
			case SUNRISE:
				this.setSunrise(value);
				break;
			case DHUHR_START:
				this.setDhuhrStart(value);
				break;
			case DHUHR_JAMAAH:
				this.setDhuhrJam(value);
				break;
			case ASR_START:
				this.setAsrStart(value);
				break;
			case ASR_JAMAAH:
				this.setAsrJam(value);
				break;
			case MAGHRIB_JAMAAH:
				this.setMaghribJam(value);
				break;
			case ISHAA_START:
				this.setIshaaStart(value);
				break;
			case ISHAA_JAMAAH:
				this.setIshaaJam(value);
				break;
		}
	}
	
    private static Optional<DayOfWeek> extractDayOfWeek(String data) {
    	return Arrays.asList(DayOfWeek.values())
		.stream()
		.filter(dayOfWeek -> data.toLowerCase().contains(dayOfWeek.name().toLowerCase()))
		.findFirst();
    }
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
