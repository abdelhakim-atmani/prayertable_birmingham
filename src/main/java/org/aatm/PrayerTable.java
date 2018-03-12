package org.aatm;

import java.util.List;
import java.util.Map;

public class PrayerTable {

	private String hijriMonth;
	private String gregorianMonth;
	private List<Map<Column, String>> prayerTimeList;

	public PrayerTable() {

	}
	
	public PrayerTable(String hijriMonth, String gregorianMonth, List<Map<Column, String>> prayerTimeList) {
		this.hijriMonth = hijriMonth;
		this.gregorianMonth = gregorianMonth;
		this.prayerTimeList = prayerTimeList;
	}

	public String getHijriMonth() {
		return hijriMonth;
	}

	public void setHijriMonth(String hijriMonth) {
		this.hijriMonth = hijriMonth;
	}

	public String getGregorianMonth() {
		return gregorianMonth;
	}

	public void setGregorianMonth(String gregorianMonth) {
		this.gregorianMonth = gregorianMonth;
	}

	public List<Map<Column, String>> getPrayerTimeList() {
		return prayerTimeList;
	}

	public void setPrayerTimeList(List<Map<Column, String>> prayerTimeList) {
		this.prayerTimeList = prayerTimeList;
	}

}
