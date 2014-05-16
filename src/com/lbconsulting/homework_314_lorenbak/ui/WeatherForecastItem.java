package com.lbconsulting.homework_314_lorenbak.ui;

import android.graphics.Bitmap;

public class WeatherForecastItem {

	private String DayName;
	private String Temperature;
	private String WeatherConditions;
	private Bitmap WeatherBitmap;

	public String getDayName() {
		return DayName;
	}

	public void setDayName(String dayName) {
		DayName = dayName;
	}

	public String getTemperature() {
		return Temperature;
	}

	public void setTemperature(String temperature) {
		Temperature = temperature;
	}

	public String getWeatherConditions() {
		return WeatherConditions;
	}

	public void setWeatherConditions(String weatherConditions) {
		WeatherConditions = weatherConditions;
	}

	public Bitmap getWeatherBitmap() {
		return WeatherBitmap;
	}

	public void setWeatherBitmap(Bitmap weatherBitmap) {
		WeatherBitmap = weatherBitmap;
	}

}
