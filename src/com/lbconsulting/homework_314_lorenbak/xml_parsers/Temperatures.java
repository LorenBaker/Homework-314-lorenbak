package com.lbconsulting.homework_314_lorenbak.xml_parsers;

import java.util.ArrayList;

public class Temperatures {

	public static final String TAG_NAME = "name";
	public static final String TAG_VALUE = "value";

	private String type;
	private String units;
	private String timeLayoutKey;
	private String name;

	private ArrayList<String> values = new ArrayList<String>();

	public Temperatures(String timeLayoutKey) {
		this.timeLayoutKey = timeLayoutKey;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getTimeLayoutKey() {
		return timeLayoutKey;
	}

	public void setTimeLayoutKey(String timeLayoutKey) {
		this.timeLayoutKey = timeLayoutKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getValues() {
		return values;
	}

	public String getValue(int index) {
		return values.get(index);
	}

}
