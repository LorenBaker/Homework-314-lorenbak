package com.lbconsulting.homework_314_lorenbak.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lbconsulting.homework_314_lorenbak.R;
import com.lbconsulting.homework_314_lorenbak.misc.DiskLruImageCache;
import com.lbconsulting.homework_314_lorenbak.misc.MyLog;
import com.lbconsulting.homework_314_lorenbak.xml_parsers.CurrentConditions;
import com.lbconsulting.homework_314_lorenbak.xml_parsers.CurrentConditions_Parser;

public class CurrentConditionsFragment extends Fragment {

	private static final String STATE_CURRENT_CONDITIONS_URL = "CurrentConditionsURL";

	private String mCurrentConditionsURL;
	private int mDisplayUnits;

	private ImageView imageCurrentConditions;
	private TextView tvCurrentTemp;
	private TextView tvCurrentConditions;
	private TextView tvCurrentHumidity;
	private TextView tvCurrentWindSpeed;
	private TextView tvCurrentVisibility;

	private static DiskLruImageCache mDiskCache;
	private static int DISK_CACHE_SIZE = 1024 * 1024 * 16; // 16mb in bytes
	private static String DISK_CACH_DIRECTORY = "HW312_Images";

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
		tvCurrentTemp = (TextView) view.findViewById(R.id.tvCurrentTemp);
		tvCurrentConditions = (TextView) view.findViewById(R.id.tvCurrentConditions);
		tvCurrentHumidity = (TextView) view.findViewById(R.id.tvCurrentHumidity);
		tvCurrentWindSpeed = (TextView) view.findViewById(R.id.tvCurrentWindSpeed);
		tvCurrentVisibility = (TextView) view.findViewById(R.id.tvCurrentVisibility);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		MyLog.i("CurrentConditionsFragment", "onActivityCreated()");
		String[] args = new String[] { mCurrentConditionsURL, String.valueOf(mDisplayUnits) };
		new LoadCurrentWeatherConditions().execute(args);

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
			// nothing to do
		}

		@Override
		protected CurrentConditions doInBackground(String... params) {
			String currentConditionsURL = params[0];
			String activeUnitsString = params[1];
			activeUnits = Integer.parseInt(activeUnitsString);

			CurrentConditions currentConditions = DownloadCurrentWeatherConditions(currentConditionsURL);
			if (currentConditions != null && !currentConditions.getIcon_url_name().isEmpty()) {
				getCurrentConditionsBitmap(currentConditions);
			}

			return currentConditions;
		}

		private void getCurrentConditionsBitmap(CurrentConditions currentConditions) {
			// TODO Auto-generated method stub

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
					if (responseCode == HttpURLConnection.HTTP_OK) {
						// Get the current conditions input stream
						InputStream currentConditionsFeedStream = httpURLConnection.getInputStream();
						// Parse the input steam and save data to a CurrentConditions object
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

		private void ShowCurentWeather(CurrentConditions currentConditions) {
			// TODO Auto-generated method stub

		}

		@Override
		protected void onPostExecute(CurrentConditions currentConditions) {
			if (currentConditions != null) {
				ShowCurentWeather(currentConditions);
			}
			super.onPostExecute(currentConditions);
		}

	}
}
