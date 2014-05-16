package com.lbconsulting.homework_314_lorenbak.xml_parsers;

import java.util.ArrayList;

public class ConditionIcons {

	// public static final String TAG_NAME = "name";
	public static final String TAG_ICON_LINK = "icon-link";

	// private String name = "";
	private String type = "";
	private String timeLayoutKey = "";
	private ArrayList<String> iconLinks = new ArrayList<String>();
	private ArrayList<String> icons = new ArrayList<String>();

	public ConditionIcons(String timeLayoutKey) {
		this.timeLayoutKey = timeLayoutKey;
	}

	/*	public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}*/

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTimeLayoutKey() {
		return timeLayoutKey;
	}

	public void setTimeLayoutKey(String timeLayoutKey) {
		this.timeLayoutKey = timeLayoutKey;
	}

	public ArrayList<String> getIconLinks() {
		return iconLinks;
	}

	public String getIconLink(int index) {
		return iconLinks.get(index);
	}

	public ArrayList<String> getIcons() {
		return icons;
	}

	public String getIcon(int index) {
		return icons.get(index);
	}
}
