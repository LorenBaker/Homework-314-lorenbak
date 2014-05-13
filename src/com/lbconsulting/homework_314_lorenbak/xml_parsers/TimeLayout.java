package com.lbconsulting.homework_314_lorenbak.xml_parsers;

import java.util.ArrayList;
import java.util.Calendar;

public class TimeLayout {

	public static final String TAG_LAYOUT_KEY = "layout-key";
	public static final String TAG_START_VALID_TIME = "start-valid-time";
	public static final String TAG_END_VALID_TIME = "end-valid-time";

	private String layout_key;
	private ArrayList<Calendar> startTimes = new ArrayList<Calendar>();
	private ArrayList<Calendar> endTimes = new ArrayList<Calendar>();
	private ArrayList<String> periodNames = new ArrayList<String>();

	public TimeLayout(String layout_key) {
		this.layout_key = layout_key;
	}

	public String getLayout_key() {
		return layout_key;
	}

	public void setLayout_key(String layout_key) {
		this.layout_key = layout_key;
	}

	public Calendar getStartTime(int index) {
		return startTimes.get(index);
	}

	public Calendar getEndTime(int index) {
		return endTimes.get(index);
	}

	public String getPeriodName(int index) {
		return periodNames.get(index);
	}

	public ArrayList<Calendar> getStartTimes() {
		return startTimes;
	}

	public ArrayList<Calendar> getEndTimes() {
		return endTimes;
	}

	public ArrayList<String> getPeriodNames() {
		return periodNames;
	}
}
