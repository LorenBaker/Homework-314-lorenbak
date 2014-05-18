package com.lbconsulting.homework_314_lorenbak.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.lbconsulting.homework_314_lorenbak.R;
import com.lbconsulting.homework_314_lorenbak.R.drawable;
import com.lbconsulting.homework_314_lorenbak.misc.DiskLruImageCache;
import com.lbconsulting.homework_314_lorenbak.misc.MyLog;
import com.lbconsulting.homework_314_lorenbak.misc.SettingsFragment;
import com.lbconsulting.homework_314_lorenbak.xml_parsers.TimeLayout;
import com.lbconsulting.homework_314_lorenbak.xml_parsers.WeatherForecast;
import com.lbconsulting.homework_314_lorenbak.xml_parsers.WeatherForecast_Parser;

public class ForecastFragment extends Fragment {

	private ForecastWeatherPostExecute mForecastWeatherPostExecuteCallback;

	// Container Activity must implement this interface
	public interface ForecastWeatherPostExecute {

		public void onForecastWeatherDownloadComplete(WeatherForecast weatherForecast);
	}

	private static final String FORECAST_WEATHER_URL = "ForecastConditionsURL";
	private static final int DAY_TIME_LAYOUT = 0;
	private static final int NIGHT_TIME_LAYOUT = 1;
	private static final int THREE_HOUR_TIME_LAYOUT = 2;

	private String mForecastConditionsURL;
	private int mDisplayUnits;
	private GridView mGridForecast;
	private WeatherForecast mWeatherForecast = null;
	private ForecastGridViewAdapter mForecastGridViewAdapter = null;
	private ArrayList<WeatherForecastItem> mWeatherForecastData = new ArrayList<WeatherForecastItem>();

	private static DiskLruImageCache mDiskCache;
	private static int DISK_CACHE_SIZE = 1024 * 1024 * 16; // 16mb in bytes
	private static String DISK_CACH_DIRECTORY = "ForecastImages";

	private LoadForecastWeatherConditions mLoadForecastWeatherConditions = null;

	public ForecastFragment() {
		// Empty constructor
	}

	public static ForecastFragment newInstance(String forecastWeatherURL) {
		ForecastFragment f = new ForecastFragment();
		Bundle args = new Bundle();
		args.putString(FORECAST_WEATHER_URL, forecastWeatherURL);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		MyLog.i("ForecastFragment", "onCreate()");
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mForecastWeatherPostExecuteCallback = (ForecastWeatherPostExecute) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement onForecastWeatherDownloadComplete callback");
		}
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		MyLog.i("ForecastFragment", "onCreateView()");

		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(FORECAST_WEATHER_URL)) {
				mForecastConditionsURL = savedInstanceState.getString(FORECAST_WEATHER_URL);
			}

		} else {
			Bundle bundle = getArguments();
			if (bundle != null) {
				mForecastConditionsURL = bundle.getString(FORECAST_WEATHER_URL);
			}
		}

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mDisplayUnits = Integer.parseInt(sharedPref.getString(SettingsFragment.KEY_DISPLAY_UNITS, ""));

		View view = inflater.inflate(R.layout.fragment_forecast, container, false);
		mGridForecast = (GridView) view.findViewById(R.id.gridForecast);
		mForecastGridViewAdapter = new ForecastGridViewAdapter(view.getContext(), R.layout.cell_forecast,
				mWeatherForecastData);
		mGridForecast.setAdapter(mForecastGridViewAdapter);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		MyLog.i("ForecastFragment", "onActivityCreated()");

		mDiskCache = new DiskLruImageCache(getActivity(), DISK_CACH_DIRECTORY, DISK_CACHE_SIZE, CompressFormat.PNG, 80);
		String[] args = new String[] { mForecastConditionsURL, String.valueOf(mDisplayUnits) };
		mLoadForecastWeatherConditions = (LoadForecastWeatherConditions) new LoadForecastWeatherConditions()
				.execute(args);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		MyLog.i("ForecastFragment", "onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		MyLog.i("ForecastFragment", "onDetach()");
		super.onDetach();
	}

	@Override
	public void onPause() {
		MyLog.i("ForecastFragment", "onPause()");
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		MyLog.i("ForecastFragment", "onSaveInstanceState()");
		outState.putString(FORECAST_WEATHER_URL, mForecastConditionsURL);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		MyLog.i("ForecastFragment", "onResume()");
		super.onResume();
	}

	private class LoadForecastWeatherConditions extends AsyncTask<String, Void, WeatherForecast> {

		int activeUnits = -1;

		@Override
		protected void onPreExecute() {
			// do nothing
		}

		@Override
		protected WeatherForecast doInBackground(String... params) {
			MyLog.i("ForecastFragment", "doInBackground(): Loading current weather conditions.");
			String weatherForecastURL = params[0];
			String activeUnitsString = params[1];
			activeUnits = Integer.parseInt(activeUnitsString);

			try {
				mWeatherForecast = DownloadWeatherForecast(weatherForecastURL);
				getForecastConditionsBitmap(mWeatherForecast);
				mWeatherForecastData = fillWeatherForecastData();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return mWeatherForecast;
		}

		private void getForecastConditionsBitmap(WeatherForecast weatherForecast) {
			if (weatherForecast != null) {
				String icon;
				String iconLink;
				Bitmap iconBitmap;
				int maxSize = weatherForecast.getConditionIcons().getIcons().size();
				for (int index = 0; index < maxSize; index++) {
					icon = weatherForecast.getConditionIcons().getIcon(index);
					if (!mDiskCache.containsKey(icon)) {
						iconLink = weatherForecast.getConditionIcons().getIconLink(index);
						iconBitmap = getImageFromWeb(iconLink);
						if (iconBitmap != null) {
							mDiskCache.put(icon, iconBitmap);
						}
					}
				}
			}
		}

		private Bitmap getImageFromWeb(String icon_url) {
			Bitmap bitmapImage = null;
			if (icon_url != null && !icon_url.isEmpty()) {
				try {
					InputStream is = (InputStream) new URL(icon_url).getContent();
					bitmapImage = BitmapFactory.decodeStream(is);
					if (is != null) {
						is.close();
					}

				} catch (Exception e) {
					MyLog.e("ForecastFragment", "ERROR in getImageFromWeb(): " + e.getMessage());
				}
			}
			return bitmapImage;
		}

		private WeatherForecast DownloadWeatherForecast(String weatherForecastURL) throws ParseException {
			WeatherForecast weatherForecast = null;
			if (weatherForecastURL != null && !weatherForecastURL.isEmpty()) {

				try {
					// Create news feed URL
					URL weatherForecastFeed = new URL(weatherForecastURL);
					// Create new HTTP URL connection
					URLConnection connection = weatherForecastFeed.openConnection();
					HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

					int responseCode = httpURLConnection.getResponseCode();
					MyLog.i("ForecastFragment",
							"DownloadWeatherForecast(): httpURLConnection responseCode:" + responseCode);
					if (responseCode == HttpURLConnection.HTTP_OK) {
						// Get the current conditions input stream
						MyLog.i("ForecastFragment",
								"DownloadWeatherForecast(): getting weather forecast");
						InputStream weatherForecastFeedStream = httpURLConnection.getInputStream();
						// Parse the input steam and save data to a ForecastConditions object
						MyLog.i("ForecastFragment",
								"DownloadWeatherForecast(): parsing weather forecast");
						weatherForecast = WeatherForecast_Parser.parse(weatherForecastFeedStream);
						weatherForecastFeedStream.close();
					}

				} catch (MalformedURLException e) {
					MyLog.e("ForecastFragment",
							"DownloadForecastWeatherConditions(): MalformedURLException opening news feed: "
									+ weatherForecastURL + ". " + e.getMessage());
				} catch (IOException e) {
					MyLog.e("ForecastFragment",
							"DownloadForecastWeatherConditions(): IOException opening news feed: "
									+ weatherForecastURL + ". " + e.getMessage());

				} catch (XmlPullParserException e) {
					MyLog.e("ForecastFragment",
							"DownloadForecastWeatherConditions(): XmlPullParserException opening news feed: "
									+ weatherForecastURL + ". " + e.getMessage());
				}

			}

			return weatherForecast;
		}

		@Override
		protected void onPostExecute(WeatherForecast weatherForecast) {
			MyLog.i("ForecastFragment", "onPostExecute(): Loading current weather conditions FINISHED.");
			if (weatherForecast != null) {
				mForecastGridViewAdapter.setWeatherForecastData(mWeatherForecastData);
				mForecastGridViewAdapter.notifyDataSetChanged();
				mForecastWeatherPostExecuteCallback.onForecastWeatherDownloadComplete(weatherForecast);
			} else {
				new AlertDialog.Builder(getActivity())
						.setTitle("Failed to get weather forecast.")
						.setMessage(
								"Did not get a response from the weather.gov website.  Please try again latter.")
						.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

							}
						})
						.setIcon(drawable.ic_action_warning)
						.show();
			}
			super.onPostExecute(weatherForecast);
		}

		private ArrayList<WeatherForecastItem> fillWeatherForecastData() {
			ArrayList<WeatherForecastItem> weatherForecastItems = null;

			String tempUnits;
			if (mWeatherForecast != null) {
				weatherForecastItems = new ArrayList<WeatherForecastItem>();

				switch (activeUnits) {
					case MainActivity.METRIC_UNITS:
						tempUnits = (char) 0x00B0 + "C";
						break;
					default:
						tempUnits = (char) 0x00B0 + "F";
						break;
				}

				// starting index of time-layout 1 = 0
				// starting index of time-layout = the index where time-layout 1's end-valid-time equals time-layout 2's
				// start-valid-time
				// time-layout 1's and 2's indexes are then incremented to point to positions 1,2,3 and positions 4,5,6
				// respectively

				// use the end-valid-time of the position to find the index in time-layout 3. This index sets both the
				// weather-conditions and Conditions Icons

				int time_layout_1_index = 0;
				int time_layout_2_index = find_time_layout_2_index();
				// ArrayList<Integer> three_hour_time_layout_indexes;
				int three_hour_time_layout_index;
				ArrayList<Integer> day_three_hour_time_layout_indexes = new ArrayList<Integer>();
				ArrayList<Integer> night_three_hour_time_layout_indexes = new ArrayList<Integer>();

				three_hour_time_layout_index = 0;

				three_hour_time_layout_index = nextDayThreeHourTimeLayoutIndex(three_hour_time_layout_index);
				day_three_hour_time_layout_indexes.add(three_hour_time_layout_index);
				three_hour_time_layout_index = nextNightThreeHourTimeLayoutIndex(three_hour_time_layout_index);
				night_three_hour_time_layout_indexes.add(three_hour_time_layout_index);

				three_hour_time_layout_index = nextDayThreeHourTimeLayoutIndex(three_hour_time_layout_index);
				day_three_hour_time_layout_indexes.add(three_hour_time_layout_index);
				three_hour_time_layout_index = nextNightThreeHourTimeLayoutIndex(three_hour_time_layout_index);
				night_three_hour_time_layout_indexes.add(three_hour_time_layout_index);

				three_hour_time_layout_index = nextDayThreeHourTimeLayoutIndex(three_hour_time_layout_index);
				day_three_hour_time_layout_indexes.add(three_hour_time_layout_index);
				three_hour_time_layout_index = nextNightThreeHourTimeLayoutIndex(three_hour_time_layout_index);
				night_three_hour_time_layout_indexes.add(three_hour_time_layout_index);

				String day_period_name;
				String night_period_name;
				String maxTemp;
				String minTemp;
				String weather_condition;
				String weatherBitmapKey;
				Bitmap weatherBitmap;

				WeatherForecastItem weatherForecastItemPosition0 = new WeatherForecastItem();
				WeatherForecastItem weatherForecastItemPosition1 = new WeatherForecastItem();
				WeatherForecastItem weatherForecastItemPosition2 = new WeatherForecastItem();
				WeatherForecastItem weatherForecastItemPosition3 = new WeatherForecastItem();
				WeatherForecastItem weatherForecastItemPosition4 = new WeatherForecastItem();
				WeatherForecastItem weatherForecastItemPosition5 = new WeatherForecastItem();

				// ************* Position 0 ************************

				TimeLayout dayTimeLayout = mWeatherForecast.getTimeLayout(DAY_TIME_LAYOUT);
				if (dayTimeLayout != null) {
					day_period_name = mWeatherForecast.getTimeLayout(DAY_TIME_LAYOUT)
							.getPeriodName(time_layout_1_index);

					three_hour_time_layout_index = day_three_hour_time_layout_indexes.get(0);

					MyLog.i("ForecastFragment", "fillWeatherForecastData(); time_layout_1_index:" + time_layout_1_index
							+ "three_hour_time_layout_index:" + three_hour_time_layout_index);

					maxTemp = "High: " + mWeatherForecast.getMaxTemperatures().getValue(0) + tempUnits;
					weather_condition = mWeatherForecast.getWeather().getConditions(three_hour_time_layout_index)
							.toString();
					weatherBitmapKey = mWeatherForecast.getConditionIcons().getIcon(three_hour_time_layout_index);

					weatherForecastItemPosition0.setDayName(day_period_name);
					weatherForecastItemPosition0.setTemperature(maxTemp);
					weatherForecastItemPosition0.setWeatherConditions(weather_condition);
					weatherBitmap = mDiskCache.getBitmap(weatherBitmapKey);
					weatherForecastItemPosition0.setWeatherBitmap(weatherBitmap);

					// ************* Position 1 ************************
					time_layout_1_index++;
					day_period_name = mWeatherForecast.getTimeLayout(DAY_TIME_LAYOUT)
							.getPeriodName(time_layout_1_index);
					three_hour_time_layout_index = day_three_hour_time_layout_indexes.get(1);

					MyLog.i("ForecastFragment", "fillWeatherForecastData(); time_layout_1_index:" + time_layout_1_index
							+ "three_hour_time_layout_index:" + three_hour_time_layout_index);

					maxTemp = "High: " + mWeatherForecast.getMaxTemperatures().getValue(1) + tempUnits;
					weather_condition = mWeatherForecast.getWeather().getConditions(three_hour_time_layout_index)
							.toString();
					weatherBitmapKey = mWeatherForecast.getConditionIcons().getIcon(three_hour_time_layout_index);

					weatherForecastItemPosition1.setDayName(day_period_name);
					weatherForecastItemPosition1.setTemperature(maxTemp);
					weatherForecastItemPosition1.setWeatherConditions(weather_condition);
					weatherBitmap = mDiskCache.getBitmap(weatherBitmapKey);
					weatherForecastItemPosition1.setWeatherBitmap(weatherBitmap);

					// ************* Position 2 ************************

					time_layout_1_index++;
					day_period_name = mWeatherForecast.getTimeLayout(DAY_TIME_LAYOUT)
							.getPeriodName(time_layout_1_index);
					three_hour_time_layout_index = day_three_hour_time_layout_indexes.get(2);

					MyLog.i("ForecastFragment", "fillWeatherForecastData(); time_layout_1_index:" + time_layout_1_index
							+ "three_hour_time_layout_index:" + three_hour_time_layout_index);

					maxTemp = "High: " + mWeatherForecast.getMaxTemperatures().getValue(2) + tempUnits;
					weather_condition = mWeatherForecast.getWeather().getConditions(three_hour_time_layout_index)
							.toString();
					weatherBitmapKey = mWeatherForecast.getConditionIcons().getIcon(three_hour_time_layout_index);

					weatherForecastItemPosition2.setDayName(day_period_name);
					weatherForecastItemPosition2.setTemperature(maxTemp);
					weatherForecastItemPosition2.setWeatherConditions(weather_condition);
					weatherBitmap = mDiskCache.getBitmap(weatherBitmapKey);
					weatherForecastItemPosition2.setWeatherBitmap(weatherBitmap);

				}
				// ************* Position 3 ************************

				TimeLayout nightTimeLayout = mWeatherForecast.getTimeLayout(NIGHT_TIME_LAYOUT);
				if (nightTimeLayout != null) {
					night_period_name = mWeatherForecast.getTimeLayout(NIGHT_TIME_LAYOUT).getPeriodName(
							time_layout_2_index);
					three_hour_time_layout_index = night_three_hour_time_layout_indexes.get(0);

					MyLog.i("ForecastFragment", "fillWeatherForecastData(); time_layout_2_index:" + time_layout_2_index
							+ "three_hour_time_layout_index:" + three_hour_time_layout_index);
					three_hour_time_layout_index = night_three_hour_time_layout_indexes.get(0);

					minTemp = "Low: " + mWeatherForecast.getMinTemperatures().getValue(0) + tempUnits;
					weather_condition = mWeatherForecast.getWeather().getConditions(three_hour_time_layout_index)
							.toString();
					weatherBitmapKey = mWeatherForecast.getConditionIcons().getIcon(three_hour_time_layout_index);

					weatherForecastItemPosition3.setDayName(night_period_name);
					weatherForecastItemPosition3.setTemperature(minTemp);
					weatherForecastItemPosition3.setWeatherConditions(weather_condition);
					weatherBitmap = mDiskCache.getBitmap(weatherBitmapKey);
					weatherForecastItemPosition3.setWeatherBitmap(weatherBitmap);

					// ************* Position 4 ************************
					time_layout_2_index++;
					night_period_name = mWeatherForecast.getTimeLayout(NIGHT_TIME_LAYOUT).getPeriodName(
							time_layout_2_index);
					three_hour_time_layout_index = night_three_hour_time_layout_indexes.get(1);

					MyLog.i("ForecastFragment", "fillWeatherForecastData(); time_layout_2_index:" + time_layout_2_index
							+ "three_hour_time_layout_index:" + three_hour_time_layout_index);

					minTemp = "Low: " + mWeatherForecast.getMinTemperatures().getValue(1) + tempUnits;
					weather_condition = mWeatherForecast.getWeather().getConditions(three_hour_time_layout_index)
							.toString();
					weatherBitmapKey = mWeatherForecast.getConditionIcons().getIcon(three_hour_time_layout_index);

					weatherForecastItemPosition4.setDayName(night_period_name);
					weatherForecastItemPosition4.setTemperature(minTemp);
					weatherForecastItemPosition4.setWeatherConditions(weather_condition);
					weatherBitmap = mDiskCache.getBitmap(weatherBitmapKey);
					weatherForecastItemPosition4.setWeatherBitmap(weatherBitmap);

					// ************* Position 5 ************************

					time_layout_2_index++;
					night_period_name = mWeatherForecast.getTimeLayout(NIGHT_TIME_LAYOUT).getPeriodName(
							time_layout_2_index);
					three_hour_time_layout_index = night_three_hour_time_layout_indexes.get(2);

					MyLog.i("ForecastFragment", "fillWeatherForecastData(); time_layout_2_index:" + time_layout_2_index
							+ "three_hour_time_layout_index:" + three_hour_time_layout_index);

					minTemp = "Low: " + mWeatherForecast.getMinTemperatures().getValue(2) + tempUnits;
					weather_condition = mWeatherForecast.getWeather().getConditions(three_hour_time_layout_index)
							.toString();
					weatherBitmapKey = mWeatherForecast.getConditionIcons().getIcon(three_hour_time_layout_index);

					weatherForecastItemPosition5.setDayName(night_period_name);
					weatherForecastItemPosition5.setTemperature(minTemp);
					weatherForecastItemPosition5.setWeatherConditions(weather_condition);
					weatherBitmap = mDiskCache.getBitmap(weatherBitmapKey);
					weatherForecastItemPosition5.setWeatherBitmap(weatherBitmap);

				}

				weatherForecastItems.add(weatherForecastItemPosition0);
				weatherForecastItems.add(weatherForecastItemPosition1);
				weatherForecastItems.add(weatherForecastItemPosition2);
				weatherForecastItems.add(weatherForecastItemPosition3);
				weatherForecastItems.add(weatherForecastItemPosition4);
				weatherForecastItems.add(weatherForecastItemPosition5);

			}

			return weatherForecastItems;
		}

		private int nextDayThreeHourTimeLayoutIndex(int startIndex) {
			int resultIndex = startIndex;
			if (mWeatherForecast != null) {
				String icon;
				int threeHourTimeLayoutSize = mWeatherForecast.getTimeLayout(THREE_HOUR_TIME_LAYOUT)
						.getStartTimes().size();

				while (resultIndex < threeHourTimeLayoutSize) {
					icon = mWeatherForecast.getConditionIcons().getIcon(resultIndex);
					if (!icon.startsWith("n")) {
						break;
					}
					resultIndex++;
				}
			}
			return resultIndex;
		}

		private int nextNightThreeHourTimeLayoutIndex(int startIndex) {
			int resultIndex = startIndex;
			if (mWeatherForecast != null) {
				String icon;
				int threeHourTimeLayoutSize = mWeatherForecast.getTimeLayout(THREE_HOUR_TIME_LAYOUT)
						.getStartTimes().size();

				while (resultIndex < threeHourTimeLayoutSize) {
					icon = mWeatherForecast.getConditionIcons().getIcon(resultIndex);
					if (icon.startsWith("n")) {
						break;
					}
					resultIndex++;
				}
			}

			return resultIndex;
		}

		private int find_time_layout_2_index() {
			int resultIndex = 0;
			long time_layout_1_end_valid_time = mWeatherForecast.getTimeLayout(DAY_TIME_LAYOUT)
					.getEndTime(0).getTimeInMillis();
			long time_layout_2_start_valid_time = mWeatherForecast.getTimeLayout(NIGHT_TIME_LAYOUT)
					.getStartTime(resultIndex).getTimeInMillis();
			int time_layout_2_start_valid_time_size = mWeatherForecast.getTimeLayout(NIGHT_TIME_LAYOUT)
					.getStartTimes().size();

			if (mWeatherForecast != null) {
				while (time_layout_1_end_valid_time >= time_layout_2_start_valid_time
						&& resultIndex < time_layout_2_start_valid_time_size) {
					resultIndex++;
					time_layout_2_start_valid_time = mWeatherForecast.getTimeLayout(NIGHT_TIME_LAYOUT)
							.getStartTime(resultIndex).getTimeInMillis();
				}
				if (resultIndex > 0) {
					resultIndex--;
				}
			}
			return resultIndex;
		}

		public AsyncTask.Status getLoadingForecastWeatherConditionsStatus() {

			AsyncTask.Status status = null;
			if (mLoadForecastWeatherConditions != null) {
				status = mLoadForecastWeatherConditions.getStatus();
			}

			return status;
		}
	}

}
