package com.lbconsulting.homework_314_lorenbak.xml_parsers;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.net.Uri;

import com.lbconsulting.homework_314_lorenbak.misc.MyLog;

public class WeatherForecast_Parser {

	private static final String ns = null;
	private static WeatherForecast weatherForecast = null;

	public WeatherForecast_Parser() {
		// TODO Auto-generated constructor stub
	}

	public static WeatherForecast parse(InputStream in)
			throws XmlPullParserException, IOException, ParseException {
		try {

			XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();

			// XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);

			parser.nextTag();
			parser.nextTag();
			skip(parser);
			parser.nextTag();
			/*int event = parser.getEventType();
			String name = parser.getName();
			*/
			if (parser.getName().equals(WeatherForecast.TAG_DATA)) {
				weatherForecast = new WeatherForecast();
				readFeed(parser);
			}

		} catch (IOException e) {
			MyLog.e("WeatherForecast_Parser", "WeatherForecast parse(): IOException");
			e.printStackTrace();

		} catch (XmlPullParserException e) {
			MyLog.e("WeatherForecast_Parser", "WeatherForecast parse(): XmlPullParserException ");
			e.printStackTrace();

		} catch (ParseException e) {
			MyLog.e("WeatherForecast_Parser", "WeatherForecast parse(): ParseException ");
			e.printStackTrace();

		} finally {
			in.close();
		}

		return weatherForecast;

	}

	private static void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
		parser.require(XmlPullParser.START_TAG, ns, WeatherForecast.TAG_DATA);

		int event;

		while ((event = parser.next()) != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (name.equals(WeatherForecast.TAG_LOCATION)) {
				parseLocation(parser);

			} else if (name.equals(WeatherForecast.TAG_MORE_WEATHER_INFO)) {
				String link = readText(parser);
				link = link.replace("&amp;", "&");
				weatherForecast.setMoreWeatherInformationLink(link);

			} else if (name.equals(WeatherForecast.TAG_TIME_LAYOUT)) {
				parseTimeLayout(parser);

			} else if (name.equals(WeatherForecast.TAG_PARAMETERS)) {
				parseParameters(parser);

			} else {
				skip(parser);
			}

		}

	}

	private static void parseLocation(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, WeatherForecast.TAG_LOCATION);

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String tagName = "location-key";
			String name = parser.getName();
			int event = parser.getEventType();
			if (name.equals(tagName)) {
				// do nothing
				String locationKey = readText(parser);

				parser.next();
				/*				name = parser.getName();
								event = parser.getEventType();*/

				// parser.next();

				/*				name = parser.getName();
								event = parser.getEventType();*/

			} else if (name.equals(WeatherForecast.TAG_POINT)) {
				HashMap<String, String> pointAttributes = getAttributes(parser);
				weatherForecast.setLatitude(pointAttributes.get("latitude"));
				weatherForecast.setLongitude(pointAttributes.get("longitude"));

				parser.next();
				/*				name = parser.getName();
								event = parser.getEventType();*/

			} else {
				skip(parser);
			}

		}

	}

	private static void parseTimeLayout(XmlPullParser parser) throws XmlPullParserException, IOException,
			ParseException {
		parser.require(XmlPullParser.START_TAG, ns, WeatherForecast.TAG_TIME_LAYOUT);

		TimeLayout timeLayout = null;

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (name.equals(TimeLayout.TAG_LAYOUT_KEY)) {
				String layout_key = readText(parser);
				timeLayout = new TimeLayout(layout_key);
				timeLayout.setLayout_key(layout_key);

			} else if (name.equals(TimeLayout.TAG_START_VALID_TIME)) {
				HashMap<String, String> startTimeAttributes = getAttributes(parser);
				if (startTimeAttributes != null && startTimeAttributes.containsKey("period-name")) {
					String periodName = startTimeAttributes.get("period-name");
					if (periodName != null && !periodName.isEmpty()) {
						if (timeLayout != null) {
							timeLayout.getPeriodNames().add(periodName);
						}
					}
				}
				String startTime = readText(parser);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
				Date date = formatter.parse(startTime);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				if (timeLayout != null) {
					timeLayout.getStartTimes().add(cal);
				}

			} else if (name.equals(TimeLayout.TAG_END_VALID_TIME)) {
				String endTime = readText(parser);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
				Date date = formatter.parse(endTime);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				if (timeLayout != null) {
					timeLayout.getEndTimes().add(cal);
				}

			} else {
				skip(parser);
			}

		}

		if (timeLayout != null) {
			weatherForecast.getTimeLayouts().add(timeLayout);
		}
	}

	private static void parseParameters(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, WeatherForecast.TAG_PARAMETERS);

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (name.equals(WeatherForecast.TAG_TEMPERATURE)) {
				parseTemperature(parser);

			} else if (name.equals(WeatherForecast.TAG_WEATHER)) {
				parseWeather(parser);

			} else if (name.equals(WeatherForecast.TAG_CONDITIONS_ICON)) {
				parseConditionsIcon(parser);

			} else {
				skip(parser);
			}
		}
	}

	private static void parseTemperature(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, WeatherForecast.TAG_TEMPERATURE);

		HashMap<String, String> temperatureAttributes = getAttributes(parser);
		String timeLayoutKey = temperatureAttributes.get("time-layout");
		Temperatures temperatures = new Temperatures(timeLayoutKey);
		temperatures.setTimeLayoutKey(timeLayoutKey);
		temperatures.setType(temperatureAttributes.get("type"));
		temperatures.setUnits(temperatureAttributes.get("units"));

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (name.equals(Temperatures.TAG_NAME)) {
				String tempName = readText(parser);
				temperatures.setName(tempName);

			} else if (name.equals(Temperatures.TAG_VALUE)) {
				String tempValue = readText(parser);
				temperatures.getValues().add(tempValue);

			} else {
				skip(parser);
			}
		}

		weatherForecast.getTemperatures().put(temperatures.getName(), temperatures);
	}

	private static void parseWeather(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, WeatherForecast.TAG_WEATHER);

		HashMap<String, String> weatherAttributes = getAttributes(parser);
		String timeLayoutKey = weatherAttributes.get("time-layout");
		Weather weather = new Weather(timeLayoutKey);

		String name = parser.getName();
		int event = parser.getEventType();

		while ((event = parser.next()) != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			event = parser.getEventType();
			name = parser.getName();
			if (name.equals(Weather.TAG_WEATHER_CONDITIONS)) {
				parseWeatherConditions(parser, weather);

			} else {
				skip(parser);
			}
		}

		weatherForecast.setWeather(weather);
	}

	private static void parseWeatherConditions(XmlPullParser parser, Weather weather) throws XmlPullParserException,
			IOException {
		parser.require(XmlPullParser.START_TAG, ns, Weather.TAG_WEATHER_CONDITIONS);

		WeatherCondition condition = new WeatherCondition();

		int event = parser.getEventType();
		while ((event = parser.next()) != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String tagVisibility = "visibility";

			String name = parser.getName();
			if (name.equals(Weather.TAG_VALUE)) {
				HashMap<String, String> conditionAttributes = getAttributes(parser);
				condition.setEmpty(false);

				if (conditionAttributes.containsKey("additive")) {
					condition.setHasAdditive(true);
					condition.setCoverage2(conditionAttributes.get("coverage"));
					condition.setIntensity2(conditionAttributes.get("intensity"));
					condition.setQualifier2(conditionAttributes.get("qualifier"));
					condition.setWeather_type2(conditionAttributes.get("weather-type"));

				} else {
					condition.setCoverage(conditionAttributes.get("coverage"));
					condition.setIntensity(conditionAttributes.get("intensity"));
					condition.setQualifier(conditionAttributes.get("qualifier"));
					condition.setWeather_type(conditionAttributes.get("weather-type"));
				}
				// parser.next();
				// name = parser.getName();
				// event = parser.getEventType();

			} else if (name.equals(tagVisibility)) {
				HashMap<String, String> visibilityAttributes = getAttributes(parser);
				// name = parser.getName();

				event = parser.next();
				// name = parser.getName();

				event = parser.next();
				// name = parser.getName();

				event = parser.next();
				// name = parser.getName();

				event = parser.next();
				// name = parser.getName();

			} else {
				skip(parser);
				// parser.next();
			}
		}

		if (condition != null) {
			weather.getWeatherConditions().add(condition);
		}

	}

	private static void parseConditionsIcon(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, WeatherForecast.TAG_CONDITIONS_ICON);

		HashMap<String, String> iconAttributes = getAttributes(parser);
		String timeLayoutKey = iconAttributes.get("time-layout");
		ConditionIcons conditionIcons = new ConditionIcons(timeLayoutKey);
		int event;

		String name = parser.getName();
		while ((event = parser.next()) != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			name = parser.getName();
			if (name.equals(ConditionIcons.TAG_ICON_LINK)) {
				String iconLink = readText(parser);
				conditionIcons.getIconLinks().add(iconLink);

				Uri iconUri = Uri.parse(iconLink);
				String icon = iconUri.getLastPathSegment();
				icon = icon.replace(".", "_");
				conditionIcons.getIcons().add(icon);

			} else {
				skip(parser);
			}
		}

		weatherForecast.setConditionIcons(conditionIcons);

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

	private static HashMap<String, String> getAttributes(XmlPullParser parser) {
		HashMap<String, String> attrs = null;
		int attributeCount = parser.getAttributeCount();
		if (attributeCount > 0) {
			attrs = new HashMap<String, String>(attributeCount);
			for (int x = 0; x < attributeCount; x++) {
				String name = parser.getAttributeName(x);
				String value = parser.getAttributeValue(x);
				if (!name.isEmpty()) {
					attrs.put(name, value);
				}
			}
		}
		else {
		}
		return attrs;
	}
}
