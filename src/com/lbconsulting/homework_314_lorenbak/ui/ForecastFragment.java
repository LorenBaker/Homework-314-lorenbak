package com.lbconsulting.homework_314_lorenbak.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lbconsulting.homework_314_lorenbak.R;
import com.lbconsulting.homework_314_lorenbak.misc.DiskLruImageCache;
import com.lbconsulting.homework_314_lorenbak.misc.MyLog;
import com.lbconsulting.homework_314_lorenbak.xml_parsers.WeatherForecast;
import com.lbconsulting.homework_314_lorenbak.xml_parsers.WeatherForecast_Parser;

public class ForecastFragment extends Fragment {

	private ForecastWeatherPostExecute mForecastWeatherPostExecuteCallback;

	// Container Activity must implement this interface
	public interface ForecastWeatherPostExecute {

		public void onForecastWeatherDownloadComplete(ForecastWeatherPostExecute currentConditions);
	}

	private static final String FORECAST_WEATHER_URL = "ForecastConditionsURL";

	private String mForecastConditionsURL;
	private int mDisplayUnits;

	/*	private ImageView imageForecastConditions;
		private TextView tvForecastConditions;
		private TextView tvHumidity;
		private TextView tvWindSpeed;
		private TextView tvBarometer;
		private TextView tvVisibility;
		private TextView tvLastUpdate;
		private TextView tvLocation;*/

	private static DiskLruImageCache mDiskCache;
	private static int DISK_CACHE_SIZE = 1024 * 1024 * 16; // 16mb in bytes
	private static String DISK_CACH_DIRECTORY = "ForecastImages";

	private LoadForecastWeatherConditions mLoadForecastWeatherConditions = null;

	public ForecastFragment() {
		// Empty constructor
	}

	public static ForecastFragment newInstance(String forecastWeatherURL, int displayUnits) {
		ForecastFragment f = new ForecastFragment();
		// Supply activeChannelID input as an argument.
		Bundle args = new Bundle();
		args.putString(FORECAST_WEATHER_URL, forecastWeatherURL);
		args.putInt(MainActivity.STATE_ACTIVE_UNITS, displayUnits);
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

			if (savedInstanceState.containsKey(MainActivity.STATE_ACTIVE_UNITS)) {
				mDisplayUnits = savedInstanceState.getInt(MainActivity.STATE_ACTIVE_UNITS);
			}

		} else {
			Bundle bundle = getArguments();
			if (bundle != null) {
				mForecastConditionsURL = bundle.getString(FORECAST_WEATHER_URL);
				mDisplayUnits = bundle.getInt(MainActivity.STATE_ACTIVE_UNITS, MainActivity.US_STANDARD_UNITS);
			}
		}

		View view = inflater.inflate(R.layout.fragment_forecast, container, false);

		/*		imageForecastConditions = (ImageView) view.findViewById(R.id.imageForecastConditions);
				tvForecastConditions = (TextView) view.findViewById(R.id.tvForecastConditions);
				tvHumidity = (TextView) view.findViewById(R.id.tvHumidity);
				tvWindSpeed = (TextView) view.findViewById(R.id.tvWindSpeed);
				tvBarometer = (TextView) view.findViewById(R.id.tvBarometer);
				tvVisibility = (TextView) view.findViewById(R.id.tvVisibility);
				tvLastUpdate = (TextView) view.findViewById(R.id.tvLastUpdate);
				tvLocation = (TextView) view.findViewById(R.id.tvLocation);*/

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		MyLog.i("ForecastFragment", "onActivityCreated()");

		mDiskCache = new DiskLruImageCache(getActivity(), DISK_CACH_DIRECTORY, DISK_CACHE_SIZE, CompressFormat.PNG, 80);

		String dataFilename = "WeatherForecast.xml";

		if (dataFilename != null && !dataFilename.isEmpty()) {
			AssetManager assetManager = getActivity().getAssets();
			if (assetManager != null) {
				InputStream weatherForecastFeedStream = null;
				try {
					weatherForecastFeedStream = assetManager.open(dataFilename);
					if (weatherForecastFeedStream != null) {
						WeatherForecast weatherForecast = WeatherForecast_Parser.parse(weatherForecastFeedStream);
						weatherForecastFeedStream.close();
					}
				} catch (IOException e) {
					MyLog.e("Titles_ACTIVITY",
							"RefreshArticles(): IOException opening " + dataFilename + "\n" + e.toString());

				} catch (XmlPullParserException e) {
					MyLog.e("Titles_ACTIVITY", "RefreshArticles(): XmlPullParserException parsing "
							+ dataFilename
							+ "\n" + e.toString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		/*		String[] args = new String[] { mForecastConditionsURL, String.valueOf(mDisplayUnits) };
				mLoadForecastWeatherConditions = (LoadForecastWeatherConditions) new LoadForecastWeatherConditions()
						.execute(args);*/
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
		outState.putInt(MainActivity.STATE_ACTIVE_UNITS, mDisplayUnits);
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

			WeatherForecast weatherForecast = null;
			try {
				weatherForecast = DownloadWeatherForecast(weatherForecastURL);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*			if (weatherForecast != null && weatherForecast.getIcon_url_name() != null
								&& !currentConditions.getIcon_url_name().isEmpty()) {
							getForecastConditionsBitmap(currentConditions);
						}*/

			return weatherForecast;
		}

		/*		private void getWeatherForecastBitmaps(WeatherForecast weatherForecast) {
					Bitmap currentConditionsBitmap = null;
					String key = currentConditions.getIcon_url_name();
					key = key.replace(".", "_");

					if (mDiskCache.containsKey(key)) {
						// load the image from storage
						currentConditionsBitmap = mDiskCache.getBitmap(key);

					} else {
						// load the image from the web
						currentConditionsBitmap = getImageFromWeb(currentConditions.getIcon_url());
						if (currentConditionsBitmap != null) {
							mDiskCache.put(key, currentConditionsBitmap);
						}
					}

					currentConditions.setIcon(currentConditionsBitmap);

				}*/

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

				/*				try {
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
								}*/

			}

			return weatherForecast;
		}

		@Override
		protected void onPostExecute(WeatherForecast weatherForecast) {
			MyLog.i("ForecastFragment", "onPostExecute(): Loading current weather conditions FINISHED.");
			/*			if (currentConditions != null) {
							ShowCurentWeather(currentConditions, activeUnits);
							mForecastWeatherDownloadCompleteCallback.onForecastWeatherDownloadComplete(currentConditions);
						} else {
							new AlertDialog.Builder(getActivity())
									.setTitle("Failed to get current weather.")
									.setMessage(
											"Did not get a response from the weather.gov website.  Please try again latter.")
									.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

										public void onClick(DialogInterface dialog, int which) {

										}
									})
									.setIcon(drawable.ic_action_warning)
									.show();
						}*/
			super.onPostExecute(weatherForecast);
		}

		/*		private void ShowCurentWeather(ForecastConditions currentConditions, int activeUnits) {
					imageForecastConditions.setImageBitmap(currentConditions.getIcon());
					String conditions = "Forecast Conditions: ";
					String humidity = "Humidity: " + currentConditions.getRelative_humidity() + "%";
					String windSpeed = "Wind: " + currentConditions.getWind_string();
					String barometer = "Barometer: ";
					String visibility = "Visibility: " + currentConditions.getVisibility_mi() + " mi";

					switch (activeUnits) {

						case MainActivity.METRIC_UNITS:
							conditions = conditions + currentConditions.getTemp_c() + (char) 0x00B0 + "C and "
									+ currentConditions.getWeather();
							barometer = barometer + currentConditions.getPressure_mb() + " mb";
							break;
						case MainActivity.US_STANDARD_UNITS:
						default:
							conditions = conditions + currentConditions.getTemp_f() + (char) 0x00B0 + "F and "
									+ currentConditions.getWeather();
							barometer = barometer + currentConditions.getPressure_in() + " in";
							break;
					}

					tvForecastConditions.setText(conditions);
					tvHumidity.setText(humidity);
					tvWindSpeed.setText(windSpeed);
					tvBarometer.setText(barometer);
					tvVisibility.setText(visibility);
					tvLastUpdate.setText(currentConditions.getObservation_time());
					tvLocation.setText(currentConditions.getLocation());

				}
			}*/

		public AsyncTask.Status getLoadingForecastWeatherConditionsStatus() {

			AsyncTask.Status status = null;
			if (mLoadForecastWeatherConditions != null) {
				status = mLoadForecastWeatherConditions.getStatus();
			}

			return status;
		}
	}

}
