package com.lbconsulting.homework_314_lorenbak.xml_parsers;

import android.graphics.Bitmap;

public class CurrentConditions {

	public static final String TAG_CURRENT_OBSERVATION = "current_observation";
	public static final String TAG_LOCATION = "location";
	public static final String TAG_OBSERVATION_TIME = "observation_time";
	public static final String TAG_WEATHER = "weather";
	public static final String TAG_TEMP_F = "temp_f";
	public static final String TAG_TEMP_C = "temp_c";
	public static final String TAG_RELATIVE_HUMIDITY = "relative_humidity";
	public static final String TAG_WIND_STRING = "wind_string";
	public static final String TAG_PRESSURE_MB = "pressure_mb";
	public static final String TAG_PRESSURE_IN = "pressure_in";
	public static final String TAG_WINDCHILL_F = "windchill_f";
	public static final String TAG_WINDCHILL_C = "windchill_c";
	public static final String TAG_VISIBILITY_MI = "visibility_mi";
	public static final String TAG_ICON_URL_NAME = "icon_url_name";

	private String location;
	private String observation_time;
	private String weather;
	private String temp_f;
	private String temp_c;
	private String relative_humidity;
	private String wind_string;
	private String pressure_mb;
	private String pressure_in;
	private String windchill_f;
	private String windchill_c;
	private String visibility_mi;
	private String icon_url_name;
	private String icon_url_base = "http://forecast.weather.gov/images/wtf/large/";
	private Bitmap icon;

	public CurrentConditions() {
		// Empty constructor
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getObservation_time() {
		return observation_time;
	}

	public void setObservation_time(String observation_time) {
		this.observation_time = observation_time;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getTemp_f() {
		return temp_f;
	}

	public void setTemp_f(String temp_f) {
		this.temp_f = temp_f;
	}

	public String getTemp_c() {
		return temp_c;
	}

	public void setTemp_c(String temp_c) {
		this.temp_c = temp_c;
	}

	public String getRelative_humidity() {
		return relative_humidity;
	}

	public void setRelative_humidity(String relative_humidity) {
		this.relative_humidity = relative_humidity;
	}

	public String getWind_string() {
		return wind_string;
	}

	public void setWind_string(String wind_string) {
		this.wind_string = wind_string;
	}

	public String getPressure_mb() {
		return pressure_mb;
	}

	public void setPressure_mb(String pressure_mb) {
		this.pressure_mb = pressure_mb;
	}

	public String getPressure_in() {
		return pressure_in;
	}

	public void setPressure_in(String pressure_in) {
		this.pressure_in = pressure_in;
	}

	public String getWindchill_f() {
		return windchill_f;
	}

	public void setWindchill_f(String windchill_f) {
		this.windchill_f = windchill_f;
	}

	public String getWindchill_c() {
		return windchill_c;
	}

	public void setWindchill_c(String windchill_c) {
		this.windchill_c = windchill_c;
	}

	public String getVisibility_mi() {
		return visibility_mi;
	}

	public void setVisibility_mi(String visibility_mi) {
		this.visibility_mi = visibility_mi;
	}

	public String getIcon_url_name() {
		return icon_url_name;
	}

	public void setIcon_url_name(String icon_url_name) {
		this.icon_url_name = icon_url_name;
	}

	public String getIcon_url_base() {
		return icon_url_base;
	}

	public String getIcon_url() {
		return icon_url_base + icon_url_name;
	}

	public Bitmap getIcon() {
		return icon;
	}

	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}

}
