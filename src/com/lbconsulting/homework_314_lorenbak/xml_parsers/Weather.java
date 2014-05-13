package com.lbconsulting.homework_314_lorenbak.xml_parsers;

import java.util.ArrayList;

public class Weather {

	// public static final String TAG_NAME = "name";
	public static final String TAG_WEATHER_CONDITIONS = "weather-conditions";
	public static final String TAG_VALUE = "value";

	private String timeLayoutKey;
	// private String name = "";
	private ArrayList<WeatherCondition> weatherConditions = new ArrayList<WeatherCondition>();

	public Weather(String timeLayoutKey) {
		this.timeLayoutKey = timeLayoutKey;
	}

	public String getTimeLayoutKey() {
		return timeLayoutKey;
	}

	public void setTimeLayoutKey(String timeLayoutKey) {
		this.timeLayoutKey = timeLayoutKey;
	}

	/*	public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}*/

	public ArrayList<WeatherCondition> getWeatherConditions() {
		return weatherConditions;
	}

	public WeatherCondition getConditions(int index) {
		return weatherConditions.get(index);
	}

}
