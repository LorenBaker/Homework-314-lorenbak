package com.lbconsulting.homework_314_lorenbak.ui;

import java.io.IOException;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lbconsulting.homework_314_lorenbak.R;
import com.lbconsulting.homework_314_lorenbak.database.ZipCodesDatabaseHelper;
import com.lbconsulting.homework_314_lorenbak.misc.MyLog;

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

	public static final String SHARED_PREFERENCES_NAME = "HW314_shared_preferences";

	public static final int STATION_1 = 1;
	public static final int STATION_2 = 2;
	public static final int STATION_3 = 3;
	private static final String STATE_ACTIVE_STATION = "ActiveStation";
	private int mActiveStation;

	public static final int US_STANDARD_UNITS = 10;
	public static final int METRIC_UNITS = 20;
	public static final String STATE_ACTIVE_UNITS = "ActiveUnits";
	private int mActiveUnits;

	private static final String STARTING_ZIP_CODE = "98006";
	public static final String STATE_ACTIVE_ZIP_CODE = "ActiveZipCode";
	private String mActiveZipCode = STARTING_ZIP_CODE;

	private Cursor mActiveZipCodesCursor;
	private ZipCodesDatabaseHelper mZipcodesDatabase;

	private static final String FRAGMENT_CURRENT_CONDITIONS = "frag_current_conditions";
	private CurrentConditionsFragment mCurrentConditionsFragment;

	private static final String FRAGMENT_FORECAST = "frag_forecast";
	private ForecastFragment mForecastFragment;

	private int ZIPCODE_CITY_LOADER_ID = 1;
	private android.app.LoaderManager mLoaderManager = null;
	private LoaderManager.LoaderCallbacks<Cursor> mZipCodesCitiesCallbacks;

	TextView tvSelectedLocation;
	EditText txtZipCode;
	ListView lvCityStateZip;

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
			if (savedInstanceState.containsKey(STATE_ACTIVE_UNITS)) {
				mActiveUnits = savedInstanceState.getInt(STATE_ACTIVE_UNITS);
			}

		} else {
			SharedPreferences storedStates = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
			mActiveZipCode = storedStates.getString(STATE_ACTIVE_ZIP_CODE, STARTING_ZIP_CODE);
			mActiveStation = storedStates.getInt(STATE_ACTIVE_STATION, STATION_2);
			mActiveUnits = storedStates.getInt(STATE_ACTIVE_UNITS, US_STANDARD_UNITS);
		}

		tvSelectedLocation = (TextView) findViewById(R.id.tvSelectedLocation);
		txtZipCode = (EditText) findViewById(R.id.txtZipCode);
		lvCityStateZip = (ListView) findViewById(R.id.lvCityStateZip);

		// Initialize the zip codes database
		mZipcodesDatabase = new ZipCodesDatabaseHelper(this);
		try {
			mZipcodesDatabase.createDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create zipcodes database");
		}

		try {
			mZipcodesDatabase.openDataBase();
		} catch (SQLException sqle) {
			throw sqle;
		}

		// get the active zip code
		refreshZipCodesCursor(mActiveZipCode);
		if (tvSelectedLocation != null) {
			tvSelectedLocation.setText(getSelectedLocation());
		}

		StartCurrentConditionsFragment();
		StartForecastFragment();

		mLoaderManager = getLoaderManager();
		mZipCodesCitiesCallbacks = this;
		mLoaderManager.initLoader(ZIPCODE_CITY_LOADER_ID, null, mZipCodesCitiesCallbacks);

	}

	private void StartCurrentConditionsFragment() {
		String currentConditionsURL = getCurrentConditionsURL(mActiveStation);
		mCurrentConditionsFragment = CurrentConditionsFragment.newInstance(currentConditionsURL, mActiveUnits);
		getFragmentManager().beginTransaction()
				.replace(R.id.current_conditions_fragment, mCurrentConditionsFragment, FRAGMENT_CURRENT_CONDITIONS)
				.commit();

	}

	private void StartForecastFragment() {
		// TODO Auto-generated method stub

	}

	private String getSelectedLocation() {
		String selectedLocation = mActiveZipCodesCursor.getString(mActiveZipCodesCursor
				.getColumnIndexOrThrow(ZipCodesDatabaseHelper.COL_LOCATION));

		return selectedLocation;
	}

	private String getCurrentConditionsURL(int stationNumber) {
		String currentConditionsURL = "";

		switch (stationNumber) {
			case STATION_1:
				currentConditionsURL = mActiveZipCodesCursor.getString(mActiveZipCodesCursor
						.getColumnIndexOrThrow(ZipCodesDatabaseHelper.COL_STATION_1_XML_URL));
				break;

			case STATION_2:
				currentConditionsURL = mActiveZipCodesCursor.getString(mActiveZipCodesCursor
						.getColumnIndexOrThrow(ZipCodesDatabaseHelper.COL_STATION_2_XML_URL));
				break;

			case STATION_3:
				currentConditionsURL = mActiveZipCodesCursor.getString(mActiveZipCodesCursor
						.getColumnIndexOrThrow(ZipCodesDatabaseHelper.COL_STATION_3_XML_URL));
				break;

			default:
				break;
		}

		return currentConditionsURL;
	}

	private void refreshZipCodesCursor(String zipCode) {
		mActiveZipCodesCursor = mZipcodesDatabase.getZipCode(zipCode);
		if (mActiveZipCodesCursor != null) {
			mActiveZipCodesCursor.moveToFirst();
		} else {
			// TODO: figure out what to do if an invalid zip code is presented.
			MyLog.e("Main_ACTIVITY", "refreshZipCodesCursor(): mActiveZipCodesCursor=null!: zipCode:" + zipCode);
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
				SelectNewZipCodeOrCity();
				// Toast.makeText(this, item.getTitle() + " is under construction.", Toast.LENGTH_SHORT).show();
				break;

			case R.id.action_refresh:
				StartCurrentConditionsFragment();
				StartForecastFragment();
				// Toast.makeText(this, item.getTitle() + " is under construction.", Toast.LENGTH_SHORT).show();
				break;

			case R.id.action_select_alternative_station:
				Toast.makeText(this, item.getTitle() + " is under construction.", Toast.LENGTH_SHORT).show();
				break;

			case R.id.action_select_display_units:
				Toast.makeText(this, item.getTitle() + " is under construction.", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;

		}
		return true;
		// return super.onOptionsItemSelected(item);
	}

	private void SelectNewZipCodeOrCity() {
		ShowTextEntryBoxAndList();

	}

	private void ShowTextEntryBoxAndList() {
		txtZipCode.setVisibility(View.VISIBLE);
		lvCityStateZip.setVisibility(View.VISIBLE);
		getFragmentManager().beginTransaction()
				.remove(mCurrentConditionsFragment)
				.commit();

	}

	@Override
	protected void onDestroy() {
		if (mActiveZipCodesCursor != null) {
			mActiveZipCodesCursor.close();
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
		storedStates.putInt(STATE_ACTIVE_UNITS, mActiveUnits);
		storedStates.commit();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(STATE_ACTIVE_ZIP_CODE, mActiveZipCode);
		outState.putInt(STATE_ACTIVE_STATION, mActiveStation);
		outState.putInt(STATE_ACTIVE_UNITS, mActiveUnits);
		super.onSaveInstanceState(outState);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		MyLog.i("Main_ACTIVITY", "onCreateLoader(): LoaderId = " + id);
		CursorLoader cursorLoader = RSS_ItemsTable.getAllItems(getActivity(), mSelectedChannelID,
				RSS_ItemsTable.SORT_ORDER_PUB_DATE);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub

	}

}
