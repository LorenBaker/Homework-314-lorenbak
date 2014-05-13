package com.lbconsulting.homework_314_lorenbak.database;

import com.lbconsulting.homework_314_lorenbak.ui.MainActivity;

public class WeatherStation {

	// private long zipCodeID;
	private int stationNumber;
	private String stationID;
	private String stationName;
	private Float stationDistance_mi;
	private Float stationDistance_km;
	private int active_units;

	public WeatherStation() {
		// TODO Auto-generated constructor stub
	}

	/*	public long getZipCodeID() {
			return zipCodeID;
		}

		public void setZipCodeID(long zipCodeID) {
			this.zipCodeID = zipCodeID;
		}
	*/
	public int getStationNumber() {
		return stationNumber;
	}

	public void setStationNumber(int stationNumber) {
		this.stationNumber = stationNumber;
	}

	public String getStationID() {
		return stationID;
	}

	public void setStationID(String stationID) {
		this.stationID = stationID;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public Float getStationDistance_mi() {
		return stationDistance_mi;
	}

	public void setStationDistance_mi(Float stationDistance_mi) {
		this.stationDistance_mi = stationDistance_mi;
	}

	public Float getStationDistance_km() {
		return stationDistance_km;
	}

	public void setStationDistance_km(Float stationDistance_km) {
		this.stationDistance_km = stationDistance_km;
	}

	public int getActive_units() {
		return active_units;
	}

	public void setActive_units(int active_units) {
		this.active_units = active_units;
	}

	@Override
	public String toString() {
		switch (active_units) {

			case MainActivity.METRIC_UNITS:
				return new StringBuilder()
						.append(stationName)
						.append(" (").append(String.format("%.1f", stationDistance_km)).append(" km)")
						.toString();

			default:
				return new StringBuilder()
						.append(stationName)
						.append(" (").append(String.format("%.1f", stationDistance_mi)).append(" mi)")
						.toString();

		}

	}

}
