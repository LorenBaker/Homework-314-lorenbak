package com.lbconsulting.homework_314_lorenbak.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lbconsulting.homework_314_lorenbak.R;
import com.lbconsulting.homework_314_lorenbak.database.ZipCodesTable;
import com.lbconsulting.homework_314_lorenbak.misc.MyLog;
import com.lbconsulting.homework_314_lorenbak.misc.SettingsFragment;
import com.lbconsulting.homework_314_lorenbak.misc.TextProgressBar;
import com.lbconsulting.homework_314_lorenbak.ui.CurrentConditionsFragment.CurrentCondtionsPostExecute;
import com.lbconsulting.homework_314_lorenbak.ui.ForecastFragment.ForecastWeatherPostExecute;
import com.lbconsulting.homework_314_lorenbak.ui.SelectStationDialogFragment.SelectWeatherStation;
import com.lbconsulting.homework_314_lorenbak.xml_parsers.CurrentConditions;
import com.lbconsulting.homework_314_lorenbak.xml_parsers.WeatherForecast;

public class MainActivity extends Activity implements CurrentCondtionsPostExecute, ForecastWeatherPostExecute,
		SelectWeatherStation, LoaderManager.LoaderCallbacks<Cursor> {

	public static final String SHARED_PREFERENCES_NAME = "HW314_shared_preferences";

	public static final int STATION_1 = 1;
	public static final int STATION_2 = 2;
	public static final int STATION_3 = 3;
	private static final String STATE_ACTIVE_STATION = "ActiveStation";
	private int mActiveStation;

	public static final int US_STANDARD_UNITS = 10;
	public static final int METRIC_UNITS = 20;

	private int mDisplayUnits;

	private static final String STARTING_ZIP_CODE = "98103";
	public static final String STATE_ACTIVE_ZIP_CODE = "ActiveZipCode";
	private String mActiveZipCode = STARTING_ZIP_CODE;

	private TextProgressBar pbLoadingIndicator;
	private LinearLayout current_conditions_fragmentLinearLayout;
	private LinearLayout forecast_fragmentLinearLayout;

	private Cursor mActiveZipCodeCursor;

	private static final String FRAGMENT_CURRENT_CONDITIONS = "frag_current_conditions";
	private CurrentConditionsFragment mCurrentConditionsFragment;

	private static final String FRAGMENT_FORECAST = "frag_forecast";
	private ForecastFragment mForecastFragment;

	private int ZIPCODE_CITY_LOADER_ID = 1;
	private android.app.LoaderManager mLoaderManager = null;
	private LoaderManager.LoaderCallbacks<Cursor> mZipCodesCitiesCallbacks;

	private boolean currentConditionsLoadingComplete = true;
	private boolean forecastLoadingComplete = true;

	TextView tvSelectedLocation;
	EditText txtZipCity;
	private ZipCityCursorAdaptor mZipCityCursorAdaptor;
	ListView lvZipCity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(STATE_ACTIVE_ZIP_CODE)) {
				mActiveZipCode = savedInstanceState.getString(STATE_ACTIVE_ZIP_CODE);
			}
			if (savedInstanceState.containsKey(STATE_ACTIVE_STATION)) {
				mActiveStation = savedInstanceState.getInt(STATE_ACTIVE_STATION);
			}
			/*			if (savedInstanceState.containsKey(STATE_ACTIVE_UNITS)) {
							mDisplayUnits = savedInstanceState.getInt(STATE_ACTIVE_UNITS);
						}*/

		} else {
			SharedPreferences storedStates = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
			mActiveZipCode = storedStates.getString(STATE_ACTIVE_ZIP_CODE, STARTING_ZIP_CODE);
			mActiveStation = storedStates.getInt(STATE_ACTIVE_STATION, STATION_1);
		}

		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		mDisplayUnits = Integer.parseInt(sharedPref.getString(SettingsFragment.KEY_DISPLAY_UNITS, ""));

		tvSelectedLocation = (TextView) findViewById(R.id.tvSelectedLocation);
		pbLoadingIndicator = (TextProgressBar) findViewById(R.id.pbLoadingIndicator);
		if (pbLoadingIndicator != null) {
			pbLoadingIndicator.setText("Downloading Weather");
		}
		txtZipCity = (EditText) findViewById(R.id.txtZipCity);

		txtZipCity.addTextChangedListener(new TextWatcher() {

			// filter master list as the user inputs text
			@Override
			public void afterTextChanged(Editable s) {

				MyLog.i("Main_ACTIVITY", "onCreate; txtZipCity.afterTextChanged -- "
						+ txtZipCity.getText().toString());
				mLoaderManager.restartLoader(ZIPCODE_CITY_LOADER_ID, null, mZipCodesCitiesCallbacks);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// Do nothing
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// Do nothing
			}
		});

		lvZipCity = (ListView) findViewById(R.id.lvZipCity);
		lvZipCity.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View listView, int position, long zipCodeID) {
				refreshZipCodesCursor(zipCodeID);
				InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				mgr.hideSoftInputFromWindow(txtZipCity.getWindowToken(), 0);
				StartCurrentConditionsFragment();
				StartForecastFragment();
			}
		}
				);

		current_conditions_fragmentLinearLayout = (LinearLayout) findViewById(R.id.current_conditions_fragment);
		forecast_fragmentLinearLayout = (LinearLayout) findViewById(R.id.forcast_fragment);

		// get the active zip code
		refreshZipCodesCursor(mActiveZipCode);

		mZipCityCursorAdaptor = new ZipCityCursorAdaptor(this, null, 0);
		lvZipCity.setAdapter(mZipCityCursorAdaptor);

		mLoaderManager = getLoaderManager();
		mZipCodesCitiesCallbacks = this;
		mLoaderManager.initLoader(ZIPCODE_CITY_LOADER_ID, null, mZipCodesCitiesCallbacks);

	}

	private void StartCurrentConditionsFragment() {
		currentConditionsLoadingComplete = false;
		HideTextEntryBoxAndList();
		ShowLoadingIndicator();
		if (mCurrentConditionsFragment != null) {
			AsyncTask.Status status = mCurrentConditionsFragment.getLoadingCurrentWeatherConditionsStatus();
			if (status == AsyncTask.Status.FINISHED) {
				ReplaceCurrentConditonsFragment();
			}
		} else {
			ReplaceCurrentConditonsFragment();
		}
	}

	private void ReplaceCurrentConditonsFragment() {
		String currentConditionsURL = getCurrentConditionsURL(mActiveStation);
		mCurrentConditionsFragment = CurrentConditionsFragment.newInstance(currentConditionsURL);

		getFragmentManager().beginTransaction()
				.replace(R.id.current_conditions_fragment, mCurrentConditionsFragment, FRAGMENT_CURRENT_CONDITIONS)
				.commit();
	}

	private void RemoveCurrentConditonsFragment() {
		getFragmentManager().beginTransaction()
				.remove(mCurrentConditionsFragment)
				.commit();
	}

	private void StartForecastFragment() {
		forecastLoadingComplete = false;
		ReplaceForecastFragment();
	}

	private void ReplaceForecastFragment() {
		String weatherForecastURL = getWeatherForecastURL();
		mForecastFragment = ForecastFragment.newInstance(weatherForecastURL);

		getFragmentManager().beginTransaction()
				.replace(R.id.forcast_fragment, mForecastFragment, FRAGMENT_FORECAST)
				.commit();

	}

	private String getWeatherForecastURL() {
		String urlPart1 = "http://graphical.weather.gov/xml/SOAP_server/ndfdXMLclient.php?whichClient=NDFDgenMultiZipCode&lat=&lon=&listLatLon=&lat1=&lon1=&lat2=&lon2=&resolutionSub=&listLat1=&listLon1=&listLat2=&listLon2=&resolutionList=&endPoint1Lat=&endPoint1Lon=&endPoint2Lat=&endPoint2Lon=&listEndPoint1Lat=&listEndPoint1Lon=&listEndPoint2Lat=&listEndPoint2Lon=&zipCodeList=";
		String urlPart2 = "&listZipCodeList=&centerPointLat=&centerPointLon=&distanceLat=&distanceLon=&resolutionSquare=&listCenterPointLat=&listCenterPointLon=&listDistanceLat=&listDistanceLon=&listResolutionSquare=&citiesLevel=&listCitiesLevel=&sector=&gmlListLatLon=&featureType=&requestedTime=&startTime=&endTime=&compType=&propertyName=&product=glance&begin=2004-01-01T00%3A00%3A00&end=2018-05-12T00%3A00%3A00&Unit=";
		String urlPart3 = "&maxt=maxt&mint=mint&wx=wx&Submit=Submit";

		StringBuilder sb = new StringBuilder();
		sb.append(urlPart1);
		sb.append(mActiveZipCode);
		sb.append(urlPart2);
		switch (mDisplayUnits) {
			case METRIC_UNITS:
				sb.append("m");
				break;
			default:
				sb.append("e");
				break;
		}

		sb.append(urlPart3);
		return sb.toString();
	}

	private void RemoveForecastFragment() {
		getFragmentManager().beginTransaction()
				.remove(mForecastFragment)
				.commit();

	}

	private String getSelectedLocation() {
		String selectedLocation = mActiveZipCodeCursor.getString(mActiveZipCodeCursor
				.getColumnIndexOrThrow(ZipCodesTable.COL_LOCATION));

		return selectedLocation;
	}

	private String getActiveZipCode() {
		String selectedLocation = mActiveZipCodeCursor.getString(mActiveZipCodeCursor
				.getColumnIndexOrThrow(ZipCodesTable.COL_ZIP_CODE));

		return selectedLocation;
	}

	private String getCurrentConditionsURL(int stationNumber) {
		String currentConditionsURL = "";

		switch (stationNumber) {
			case STATION_1:
				currentConditionsURL = mActiveZipCodeCursor.getString(mActiveZipCodeCursor
						.getColumnIndexOrThrow(ZipCodesTable.COL_STATION_1_XML_URL));
				break;

			case STATION_2:
				currentConditionsURL = mActiveZipCodeCursor.getString(mActiveZipCodeCursor
						.getColumnIndexOrThrow(ZipCodesTable.COL_STATION_2_XML_URL));
				break;

			case STATION_3:
				currentConditionsURL = mActiveZipCodeCursor.getString(mActiveZipCodeCursor
						.getColumnIndexOrThrow(ZipCodesTable.COL_STATION_3_XML_URL));
				break;

			default:
				break;
		}

		return currentConditionsURL;
	}

	private void refreshZipCodesCursor(String zipCode) {
		mActiveZipCodeCursor = ZipCodesTable.getZipCode(this, zipCode);
		if (mActiveZipCodeCursor != null) {
			mActiveZipCodeCursor.moveToFirst();
			if (tvSelectedLocation != null) {
				tvSelectedLocation.setText(getSelectedLocation());
			}
			mActiveStation = mActiveZipCodeCursor.getInt(mActiveZipCodeCursor
					.getColumnIndexOrThrow(ZipCodesTable.COL_ACTIVE_STATION_NUMBER));

		} else {
			// TODO: figure out what to do if an invalid zip code is presented.
			MyLog.e("Main_ACTIVITY", "refreshZipCodesCursor(): mActiveZipCodeCursor=null!: zipCode:" + zipCode);
		}
	}

	private void refreshZipCodesCursor(long zipCodeID) {
		mActiveZipCodeCursor = ZipCodesTable.getZipCode(this, zipCodeID);
		if (mActiveZipCodeCursor != null) {
			mActiveZipCodeCursor.moveToFirst();
			if (tvSelectedLocation != null) {
				tvSelectedLocation.setText(getSelectedLocation());
			}
			mActiveZipCode = getActiveZipCode();
			mActiveStation = mActiveZipCodeCursor.getInt(mActiveZipCodeCursor
					.getColumnIndexOrThrow(ZipCodesTable.COL_ACTIVE_STATION_NUMBER));
		} else {
			// TODO: figure out what to do if an invalid zip code is presented.
			MyLog.e("Main_ACTIVITY", "refreshZipCodesCursor(): mActiveZipCodeCursor=null!: zipCodeID:" + zipCodeID);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			case R.id.action_select_new_location:
				ShowTextEntryBoxAndList();
				// Toast.makeText(this, item.getTitle() + " is under construction.", Toast.LENGTH_SHORT).show();
				break;

			case R.id.action_refresh:
				StartCurrentConditionsFragment();
				StartForecastFragment();
				// Toast.makeText(this, item.getTitle() + " is under construction.", Toast.LENGTH_SHORT).show();
				break;

			case R.id.action_select_alternative_station:
				SelectAlternativeStation();
				// Toast.makeText(this, item.getTitle() + " is under construction.", Toast.LENGTH_SHORT).show();
				break;

			case R.id.action_settings:

				// Start the Settings Activity
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);

				// Toast.makeText(this, item.getTitle() + " is under construction.", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;

		}
		return true;
	}

	private void SelectAlternativeStation() {

		// DialogFragment.show() will take care of adding the fragment
		// in a transaction. We also want to remove any currently showing
		// dialog, so make our own transaction and take care of that here.
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag("dialog_selectStation");
		if (prev != null) {
			fragmentTransaction.remove(prev);
		}
		// Create and show the dialog.
		SelectStationDialogFragment newFragment = SelectStationDialogFragment
				.newInstance(mActiveZipCode, mDisplayUnits);
		newFragment.show(fragmentTransaction, "dialog_selectStation");

	}

	private void ShowTextEntryBoxAndList() {
		txtZipCity.setVisibility(View.VISIBLE);
		lvZipCity.setVisibility(View.VISIBLE);
		pbLoadingIndicator.setVisibility(View.GONE);
		RemoveCurrentConditonsFragment();
		RemoveForecastFragment();
	}

	private void HideTextEntryBoxAndList() {
		txtZipCity.setVisibility(View.GONE);
		txtZipCity.setText("");
		lvZipCity.setVisibility(View.GONE);
		pbLoadingIndicator.setVisibility(View.GONE);
	}

	public void ShowLoadingIndicator() {

		if (pbLoadingIndicator != null) {
			pbLoadingIndicator.setVisibility(View.VISIBLE);
			tvSelectedLocation.setVisibility(View.GONE);
			txtZipCity.setVisibility(View.GONE);
			lvZipCity.setVisibility(View.GONE);
			current_conditions_fragmentLinearLayout.setVisibility(View.GONE);
			forecast_fragmentLinearLayout.setVisibility(View.GONE);
		}
	}

	public void DismissLoadingIndicator() {

		if (pbLoadingIndicator != null) {
			pbLoadingIndicator.setVisibility(View.GONE);
			tvSelectedLocation.setVisibility(View.VISIBLE);
			txtZipCity.setVisibility(View.GONE);
			lvZipCity.setVisibility(View.GONE);
			current_conditions_fragmentLinearLayout.setVisibility(View.VISIBLE);
			forecast_fragmentLinearLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		if (mActiveZipCodeCursor != null) {
			mActiveZipCodeCursor.close();
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		MyLog.i("Main_ACTIVITY", "onPause()");

		SharedPreferences settings = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
		SharedPreferences.Editor storedStates = settings.edit();
		storedStates.putString(STATE_ACTIVE_ZIP_CODE, mActiveZipCode);
		storedStates.putInt(STATE_ACTIVE_STATION, mActiveStation);
		storedStates.commit();
		super.onPause();
	}

	@Override
	protected void onResume() {
		StartCurrentConditionsFragment();
		StartForecastFragment();
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(STATE_ACTIVE_ZIP_CODE, mActiveZipCode);
		outState.putInt(STATE_ACTIVE_STATION, mActiveStation);
		super.onSaveInstanceState(outState);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cursorLoader = null;
		MyLog.i("Main_ACTIVITY", "onCreateLoader(): LoaderId = " + id);
		String zipCityText = txtZipCity.getText().toString().trim();
		cursorLoader = ZipCodesTable.getZipCityList(this, zipCityText);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
		int id = loader.getId();
		MyLog.i("Main_ACTIVITY", "onLoadFinished(); id = " + id);
		mZipCityCursorAdaptor.swapCursor(newCursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		int id = loader.getId();
		MyLog.i("Main_ACTIVITY", "onLoaderReset(); id = " + id);
		mZipCityCursorAdaptor.swapCursor(null);
	}

	@Override
	public void onCurrentWeatherDownloadComplete(CurrentConditions currentConditions) {
		MyLog.i("Main_ACTIVITY", "onCurrentWeatherDownloadComplete()");
		currentConditionsLoadingComplete = true;
		if (forecastLoadingComplete) {
			DismissLoadingIndicator();
		}
	}

	@Override
	public void onForecastWeatherDownloadComplete(WeatherForecast weatherForecast) {
		MyLog.i("Main_ACTIVITY", "onForecastWeatherDownloadComplete()");
		forecastLoadingComplete = true;
		if (currentConditionsLoadingComplete) {
			DismissLoadingIndicator();
		}
	}

	@Override
	public void onWeatherSationChange(int stationNumber) {
		mActiveStation = stationNumber;
		StartCurrentConditionsFragment();
	}
}
