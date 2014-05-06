package com.lbconsulting.homework_314_lorenbak.xml_parsers;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class CurrentConditions_Parser {

	private static final String ns = null;
	private static CurrentConditions currentConditions = null;

	public CurrentConditions_Parser() {
		// TODO Auto-generated constructor stub
	}

	public static CurrentConditions parse(InputStream in)
			throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();

			while (!parser.getName().equals(CurrentConditions.TAG_CURRENT_OBSERVATION)) {
				parser.nextTag();
			}
			currentConditions = new CurrentConditions();
			readFeed(parser);
		} finally {
			in.close();
		}
		return currentConditions;

	}

	private static void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, CurrentConditions.TAG_CURRENT_OBSERVATION);

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (name.equals(CurrentConditions.TAG_ICON_URL_NAME)) {
				currentConditions.setIcon_url_name(readText(parser));

			} else if (name.equals(CurrentConditions.TAG_LOCATION)) {
				currentConditions.setLocation(readText(parser));

			} else if (name.equals(CurrentConditions.TAG_OBSERVATION_TIME)) {
				currentConditions.setObservation_time(readText(parser));

			} else if (name.equals(CurrentConditions.TAG_PRESSURE_IN)) {
				currentConditions.setPressure_in(readText(parser));

			} else if (name.equals(CurrentConditions.TAG_PRESSURE_MB)) {
				currentConditions.setPressure_mb(readText(parser));

			} else if (name.equals(CurrentConditions.TAG_RELATIVE_HUMIDITY)) {
				currentConditions.setRelative_humidity(readText(parser));

			} else if (name.equals(CurrentConditions.TAG_TEMP_C)) {
				currentConditions.setTemp_c(readText(parser));

			} else if (name.equals(CurrentConditions.TAG_TEMP_F)) {
				currentConditions.setTemp_f(readText(parser));

			} else if (name.equals(CurrentConditions.TAG_VISIBILITY_MI)) {
				currentConditions.setVisibility_mi(readText(parser));

			} else if (name.equals(CurrentConditions.TAG_WEATHER)) {
				currentConditions.setWeather(readText(parser));

			} else if (name.equals(CurrentConditions.TAG_WIND_STRING)) {
				currentConditions.setWind_string(readText(parser));

			} else if (name.equals(CurrentConditions.TAG_WINDCHILL_C)) {
				currentConditions.setWindchill_c(readText(parser));

			} else if (name.equals(CurrentConditions.TAG_WINDCHILL_F)) {
				currentConditions.setWindchill_f(readText(parser));

			} else {
				skip(parser);
			}

		}

	}

	private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
				case XmlPullParser.END_TAG:
					depth--;
					break;
				case XmlPullParser.START_TAG:
					depth++;
					break;
				default:
					break;
			}
		}
	}

	// extracts text values.
	private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String result = null;
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}
}
