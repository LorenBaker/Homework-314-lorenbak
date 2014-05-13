package com.lbconsulting.homework_314_lorenbak.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lbconsulting.homework_314_lorenbak.R;
import com.lbconsulting.homework_314_lorenbak.R.drawable;
import com.lbconsulting.homework_314_lorenbak.misc.DiskLruImageCache;
import com.lbconsulting.homework_314_lorenbak.misc.MyLog;
import com.lbconsulting.homework_314_lorenbak.xml_parsers.CurrentConditions;
import com.lbconsulting.homework_314_lorenbak.xml_parsers.CurrentConditions_Parser;

public class CurrentConditionsFragment extends Fragment {

	private CurrentCondtionsPostExecute mCurrentWeatherDownloadCompleteCallback;

	// Container Activity must implement this interface
	public interface CurrentCondtionsPostExecute {

		public void onCurrentWeatherDownloadComplete(CurrentConditions currentConditions);
	}

	private static final String STATE_CURRENT_CONDITIONS_URL = "CurrentConditionsURL";

	private String mCurrentConditionsURL;
	private int mDisplayUnits;

	private ImageView imageCurrentConditions;
	private TextView tvCurrentConditions;
	private TextView tvHumidity;
	private TextView tvWindSpeed;
	private TextView tvBarometer;
	private TextView tvVisibility;
	private TextView tvLastUpdate;
	private TextView tvLocation;

	private static DiskLruImageCache mDiskCache;
	private static int DISK_CACHE_SIZE = 1024 * 1024 * 16; // 16mb in bytes
	private static String DISK_CACH_DIRECTORY = "CurrentConditionsImages";

	private LoadCurrentWeatherConditions mLoadCurrentWeatherConditions = null;

	public CurrentConditionsFragment() {
		// Empty constructor
	}

	public static CurrentConditionsFragment newInstance(String currentConditionsURL, int displayUnits) {
		CurrentConditionsFragment f = new CurrentConditionsFragment();
		// Supply activeChannelID input as an argument.
		Bundle args = new Bundle();
		args.putString(STATE_CURRENT_CONDITIONS_URL, currentConditionsURL);
		args.putInt(MainActivity.STATE_ACTIVE_UNITS, displayUnits);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		MyLog.i("CurrentConditionsFragment", "onCreate()");
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCurrentWeatherDownloadCompleteCallback = (CurrentCondtionsPostExecute) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement onCurrentWeatherDownloadComplete callback");
		}
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		MyLog.i("CurrentConditionsFragment", "onCreateView()");

		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(STATE_CURRENT_CONDITIONS_URL)) {
				mCurrentConditionsURL = savedInstanceState.getString(STATE_CURRENT_CONDITIONS_URL);
			}

			if (savedInstanceState.containsKey(MainActivity.STATE_ACTIVE_UNITS)) {
				mDisplayUnits = savedInstanceState.getInt(MainActivity.STATE_ACTIVE_UNITS);
			}

		} else {
			Bundle bundle = getArguments();
			if (bundle != null) {
				mCurrentConditionsURL = bundle.getString(STATE_CURRENT_CONDITIONS_URL);
				mDisplayUnits = bundle.getInt(MainActivity.STATE_ACTIVE_UNITS, MainActivity.US_STANDARD_UNITS);
			}
		}

		View view = inflater.inflate(R.layout.fragment_current_conditions, container, false);

		imageCurrentConditions = (ImageView) view.findViewById(R.id.imageCurrentConditions);
		tvCurrentConditions = (TextView) view.findViewById(R.id.tvCurrentConditions);
		tvHumidity = (TextView) view.findViewById(R.id.tvHumidity);
		tvWindSpeed = (TextView) view.findViewById(R.id.tvWindSpeed);
		tvBarometer = (TextView) view.findViewById(R.id.tvBarometer);
		tvVisibility = (TextView) view.findViewById(R.id.tvVisibility);
		tvLastUpdate = (TextView) view.findViewById(R.id.tvLastUpdate);
		tvLocation = (TextView) view.findViewById(R.id.tvLocation);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		MyLog.i("CurrentConditionsFragment", "onActivityCreated()");

		mDiskCache = new DiskLruImageCache(getActivity(), DISK_CACH_DIRECTORY, DISK_CACHE_SIZE, CompressFormat.PNG, 80);
		// TODO: resume Current weather feed
		/*		String[] args = new String[] { mCurrentConditionsURL, String.valueOf(mDisplayUnits) };
				mLoadCurrentWeatherConditions = (LoadCurrentWeatherConditions) new LoadCurrentWeatherConditions().execute(args);*/
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		MyLog.i("CurrentConditionsFragment", "onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		MyLog.i("CurrentConditionsFragment", "onDetach()");
		super.onDetach();
	}

	@Override
	public void onPause() {
		MyLog.i("CurrentConditionsFragment", "onPause()");
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		MyLog.i("CurrentConditionsFragment", "onSaveInstanceState()");
		outState.putString(STATE_CURRENT_CONDITIONS_URL, mCurrentConditionsURL);
		outState.putInt(MainActivity.STATE_ACTIVE_UNITS, mDisplayUnits);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		MyLog.i("CurrentConditionsFragment", "onResume()");
		super.onResume();
	}

	private class LoadCurrentWeatherConditions extends AsyncTask<String, Void, CurrentConditions> {

		int activeUnits = -1;

		@Override
		protected void onPreExecute() {
			// do nothing
		}

		@Override
		protected CurrentConditions doInBackground(String... params) {
			MyLog.i("CurrentConditionsFragment", "doInBackground(): Loading current weather conditions.");
			String currentConditionsURL = params[0];
			String activeUnitsString = params[1];
			activeUnits = Integer.parseInt(activeUnitsString);

			CurrentConditions currentConditions = DownloadCurrentWeatherConditions(currentConditionsURL);
			if (currentConditions != null && currentConditions.getIcon_url_name() != null
					&& !currentConditions.getIcon_url_name().isEmpty()) {
				getCurrentConditionsBitmap(currentConditions);
			}

			return currentConditions;
		}

		private void getCurrentConditionsBitmap(CurrentConditions currentConditions) {
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
					MyLog.e("CurrentConditionsFragment", "ERROR in getImageFromWeb(): " + e.getMessage());
				}
			}
			return bitmapImage;
		}

		private CurrentConditions DownloadCurrentWeatherConditions(String currentConditionsURL) {
			CurrentConditions currentConditions = null;
			if (currentConditionsURL != null && !currentConditionsURL.isEmpty()) {
				try {
					// Create news feed URL
					URL currentConditionsFeed = new URL(currentConditionsURL);
					// Create new HTTP URL connection
					URLConnection connection = currentConditionsFeed.openConnection();
					HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

					int responseCode = httpURLConnection.getResponseCode();
					MyLog.i("CurrentConditionsFragment",
							"DownloadCurrentWeatherConditions(): httpURLConnection responseCode:" + responseCode);
					if (responseCode == HttpURLConnection.HTTP_OK) {
						// Get the current conditions input stream
						MyLog.i("CurrentConditionsFragment",
								"DownloadCurrentWeatherConditions(): getting current weather conditions");
						InputStream currentConditionsFeedStream = httpURLConnection.getInputStream();
						// Parse the input steam and save data to a CurrentConditions object
						MyLog.i("CurrentConditionsFragment",
								"DownloadCurrentWeatherConditions(): parsing current weather conditions");
						currentConditions = CurrentConditions_Parser.parse(currentConditionsFeedStream);
						currentConditionsFeedStream.close();
					}

				} catch (MalformedURLException e) {
					MyLog.e("CurrentConditionsFragment",
							"DownloadCurrentWeatherConditions(): MalformedURLException opening news feed: "
									+ currentConditionsURL + ". " + e.getMessage());
				} catch (IOException e) {
					MyLog.e("CurrentConditionsFragment",
							"DownloadCurrentWeatherConditions(): IOException opening news feed: "
									+ currentConditionsURL + ". " + e.getMessage());

				} catch (XmlPullParserException e) {
					MyLog.e("CurrentConditionsFragment",
							"DownloadCurrentWeatherConditions(): XmlPullParserException opening news feed: "
									+ currentConditionsURL + ". " + e.getMessage());
				}

			}

			return currentConditions;
		}

		@Override
		protected void onPostExecute(CurrentConditions currentConditions) {
			MyLog.i("CurrentConditionsFragment", "onPostExecute(): Loading current weather conditions FINISHED.");
			if (currentConditions != null) {
				ShowCurentWeather(currentConditions, activeUnits);
				mCurrentWeatherDownloadCompleteCallback.onCurrentWeatherDownloadComplete(currentConditions);
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
			}
			super.onPostExecute(currentConditions);
		}

		private void ShowCurentWeather(CurrentConditions currentConditions, int activeUnits) {
			imageCurrentConditions.setImageBitmap(currentConditions.getIcon());
			String conditions = "Current Conditions: ";
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

			tvCurrentConditions.setText(conditions);
			tvHumidity.setText(humidity);
			tvWindSpeed.setText(windSpeed);
			tvBarometer.setText(barometer);
			tvVisibility.setText(visibility);
			tvLastUpdate.setText(currentConditions.getObservation_time());
			tvLocation.setText(currentConditions.getLocation());

		}
	}

	public AsyncTask.Status getLoadingCurrentWeatherConditionsStatus() {

		AsyncTask.Status status = null;
		if (mLoadCurrentWeatherConditions != null) {
			status = mLoadCurrentWeatherConditions.getStatus();
		}

		return status;
	}
}
