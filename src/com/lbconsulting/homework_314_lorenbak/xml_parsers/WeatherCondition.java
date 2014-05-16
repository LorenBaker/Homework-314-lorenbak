package com.lbconsulting.homework_314_lorenbak.xml_parsers;

public class WeatherCondition {

	// private String name = "";
	private String coverage = "N/A";
	private String intensity = "N/A";
	private String weather_type = "N/A";
	private String qualifier = "N/A";

	private boolean hasAdditive = false;
	private String coverage2 = "N/A";
	private String intensity2 = "N/A";
	private String weather_type2 = "N/A";
	private String qualifier2 = "N/A";

	private boolean isEmpty = true;

	public WeatherCondition() {

	}

	// Weather conditions possible values:
	// coverage = areas, chance, definitely, isolated, likely, none, numerous, occasional, patchy, scattered, slight
	// chance, widespread

	// intensity = heavy, light, moderate, none, very light

	// weather-type = blowing dust, blowing sand, blowing snow, drizzle, fog, freezing drizzle, freezing fog, freezing
	// rain, freezing spray,
	// frost, hail, haze , ice crystals, ice fog, ice pellets, none, rain, rain shower, smoke, snow, snow shower,
	// thunderstorms,
	// volcanic ash, water spouts

	// qualifier = damaging winds, dry, frequent lightning, gusty winds, heavy rain, highest ranking, include
	// unconditionally, large hail,
	// mixture, none, on bridges and overpasses, on grassy areas, or, outlying areas, small hail, tornado

	// typical xml:
	/*<value coverage="slight chance" intensity="light" weather-type="rain showers" qualifier="none">
	        <visibility xsi:nil="true"/>
	  </value>
	  <value coverage="isolated" intensity="none" additive="and" weather-type="thunderstorms" qualifier="none">
	        <visibility xsi:nil="true"/>
	  </value>
		}*/

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (!isEmpty) {
			if (!getIntensity().equals("none")) {
				sb.append(getIntensity()).append(" ");
			}
			sb.append(getWeather_type());

			if (hasAdditive) {
				sb.append(" and ");
				if (!getIntensity2().equals("none")) {
					sb.append(getIntensity2()).append(" ");
				}
				sb.append(getWeather_type2());
			}
		} else {
			sb.append("");
		}
		return sb.toString();
	}

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
