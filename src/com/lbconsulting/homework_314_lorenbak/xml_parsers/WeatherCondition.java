package com.lbconsulting.homework_314_lorenbak.xml_parsers;

public class WeatherCondition {

	// private String name = "";
	private String coverage = "";
	private String intensity = "";
	private String weather_type = "";
	private String qualifier = "";

	private boolean hasAdditive = false;
	private String coverage2 = "";
	private String intensity2 = "";
	private String weather_type2 = "";
	private String qualifier2 = "";

	private boolean isEmpty = true;

	public WeatherCondition() {

	}

	/*<value coverage="slight chance" intensity="light" weather-type="rain showers" qualifier="none">
	        <visibility xsi:nil="true"/>
	  </value>
	  <value coverage="isolated" intensity="none" additive="and" weather-type="thunderstorms" qualifier="none">
	        <visibility xsi:nil="true"/>
	  </value>
		}*/

	public boolean isEmpty() {
		return isEmpty;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	public String getCoverage() {
		return coverage;
	}

	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}

	public String getIntensity() {
		return intensity;
	}

	public void setIntensity(String intensity) {
		this.intensity = intensity;
	}

	public String getWeather_type() {
		return weather_type;
	}

	public void setWeather_type(String weather_type) {
		this.weather_type = weather_type;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public String getCoverage2() {
		return coverage2;
	}

	public void setCoverage2(String coverage2) {
		this.coverage2 = coverage2;
	}

	public String getIntensity2() {
		return intensity2;
	}

	public void setIntensity2(String intensity2) {
		this.intensity2 = intensity2;
	}

	public String getWeather_type2() {
		return weather_type2;
	}

	public void setWeather_type2(String weather_type2) {
		this.weather_type2 = weather_type2;
	}

	public String getQualifier2() {
		return qualifier2;
	}

	public void setQualifier2(String qualifier2) {
		this.qualifier2 = qualifier2;
	}

	public boolean hasAdditive() {
		return hasAdditive;
	}

	public void setHasAdditive(boolean hasAdditive) {
		this.hasAdditive = hasAdditive;
	}

}
