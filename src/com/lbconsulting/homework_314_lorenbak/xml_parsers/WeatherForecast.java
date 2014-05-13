package com.lbconsulting.homework_314_lorenbak.xml_parsers;

import java.util.HashMap;

public class WeatherForecast {

	public static final String TAG_DWML = "dwml";
	public static final String TAG_DATA = "data";
	public static final String TAG_LOCATION = "location";
	public static final String TAG_POINT = "point";
	public static final String TAG_MORE_WEATHER_INFO = "moreWeatherInformation";
	public static final String TAG_TIME_LAYOUT = "time-layout";
	public static final String TAG_PARAMETERS = "parameters";
	public static final String TAG_TEMPERATURE = "temperature";
	public static final String TAG_WEATHER = "weather";
	public static final String TAG_CONDITIONS_ICON = "conditions-icon";

	private String latitude;
	private String longitude;
	private String moreWeatherInformationLink;
	private HashMap<String, TimeLayout> timeLayouts = new HashMap<String, TimeLayout>();
	private HashMap<String, Temperatures> temperatures = new HashMap<String, Temperatures>();
	private Weather weather;
	private ConditionIcons conditionIcons;

	public WeatherForecast() {
		// TODO Auto-generated constructor stub
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getMoreWeatherInformationLink() {
		return moreWeatherInformationLink;
	}

	public void setMoreWeatherInformationLink(String moreWeatherInformationLink) {
		this.moreWeatherInformationLink = moreWeatherInformationLink;
	}

	public TimeLayout getTimeLayout(String key) {
		return timeLayouts.get(key);
	}

	public HashMap<String, TimeLayout> getTimeLayouts() {
		return timeLayouts;
	}

	public Temperatures getMaxTemperatures() {
		return temperatures.get("Daily Maximum Temperature");
	}

	public HashMap<String, Temperatures> getTemperatures() {
		return temperatures;
	}

	public Temperatures getMinTemperatures() {
		return temperatures.get("Daily Minimum Temperature");
	}

	public Weather getWeather() {
		return weather;
	}

	public void setWeather(Weather weather) {
		this.weather = weather;
	}

	public ConditionIcons getConditionIcons() {
		return conditionIcons;
	}

	public void setConditionIcons(ConditionIcons conditionIcons) {
		this.conditionIcons = conditionIcons;
	}

}
