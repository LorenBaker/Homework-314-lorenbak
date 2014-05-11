package com.lbconsulting.homework_314_lorenbak.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;

import com.lbconsulting.homework_314_lorenbak.misc.MyLog;

public class ZipCodesTable {

	// Items data table
	// Version 1
	public static String ZIP_CODES_TABLE = "zipcodes_with_stations";

	public static String COL_ID = "_id";

	public static final String TABLE_ZIP_CODES = "zipcodes_with_stations";

	public static final String COL_ZIP_CODE_ID = "_id";
	public static String COL_ZIP_CODE = "ZipCode";
	public static String COL_LOCATION = "Location";
	public static String COL_LATITUDE = "Latitude";
	public static String COL_LONGITUDE = "Longitude";

	public static String COL_STATION_1_ID = "station1_id";
	public static String COL_STATION_1_NAME = "station1_name";
	public static String COL_STATION_1_XML_URL = "station1_xml_url";
	public static String COL_STATION_1_DISTANCE_MI = "station1_distance_miles";
	public static String COL_STATION_1_DISTANCE_KM = "station1_distance_km";

	public static String COL_STATION_2_ID = "station2_id";
	public static String COL_STATION_2_NAME = "station2_name";
	public static String COL_STATION_2_XML_URL = "station2_xml_url";
	public static String COL_STATION_2_DISTANCE_MI = "station2_distance_miles";
	public static String COL_STATION_2_DISTANCE_KM = "station2_distance_km";

	public static String COL_STATION_3_ID = "station3_id";
	public static String COL_STATION_3_NAME = "station3_name";
	public static String COL_STATION_3_XML_URL = "station3_xml_url";
	public static String COL_STATION_3_DISTANCE_MI = "station3_distance_miles";
	public static String COL_STATION_3_DISTANCE_KM = "station3_distance_km";

	public static String COL_ACTIVE_STATION_NUMBER = "active_station_number";

	public static final String[] PROJECTION_ALL = { "*" };
	public static final String[] PROJECTION_LOCATION = { COL_ZIP_CODE_ID, COL_LOCATION };

	public static final String CONTENT_PATH = "ZipcodesWithStations";

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting."
			+ CONTENT_PATH;
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting."
			+ CONTENT_PATH;
	public static final Uri CONTENT_URI = Uri.parse("content://" + ZipCodesContentProvider.AUTHORITY + "/"
			+ CONTENT_PATH);

	public static final String SORT_ORDER_LOCATION = COL_LOCATION + " ASC";
	public static final String SORT_ORDER_ZIP_CODE = COL_ZIP_CODE + " ASC";

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Create Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Read Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static Cursor getZipCode(Context context, String zipCode) {

		Uri uri = CONTENT_URI;
		String[] projection = PROJECTION_ALL;
		String selection = COL_ZIP_CODE + " = ?";
		String selectionArgs[] = new String[] { zipCode };
		String sortOrder = SORT_ORDER_LOCATION;

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("ZipCodesTable", "Exception error in getZipCode:");
			e.printStackTrace();
		}
		return cursor;
	}

	public static Cursor getZipCode(Context context, long zipCodeID) {
		Uri uri = CONTENT_URI;
		String[] projection = PROJECTION_ALL;
		String selection = COL_ZIP_CODE_ID + " = ?";
		String selectionArgs[] = new String[] { String.valueOf(zipCodeID) };
		String sortOrder = SORT_ORDER_LOCATION;

		ContentResolver cr = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("ZipCodesTable", "Exception error in getZipCode:");
			e.printStackTrace();
		}
		return cursor;

	}

	/*	public static Cursor getZipCities(Context context, String zipCityText) {
			Uri uri = CONTENT_URI;
			String[] projection = PROJECTION_LOCATION;
			String selection =  COL_LOCATION + " Like '" + zipCityText + "%'";
			String selectionArgs[] = null;
			String sortOrder = SORT_ORDER_LOCATION;

			ContentResolver cr = context.getContentResolver();
			Cursor cursor = null;
			try {
				cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
			} catch (Exception e) {
				MyLog.e("ZipCodesTable", "Exception error in getZipCities:");
				e.printStackTrace();
			}
			return cursor;
		}*/

	public static CursorLoader getZipCityList(Context context, String zipCityText) {

		CursorLoader cursorLoader = null;

		Uri uri = CONTENT_URI;
		String[] projection = PROJECTION_LOCATION;
		String selection = null;
		String selectionArgs[] = null;
		String sortOrder = SORT_ORDER_LOCATION;

		if (zipCityText.matches("^[a-zA-Z].*")) {
			// zipCityText starts with a letter
			selection = COL_LOCATION + " Like '" + zipCityText + "%'";
			sortOrder = SORT_ORDER_LOCATION;

		} else if (zipCityText.matches("^[0-9].*")) {
			// zipCityText starts with a number
			selection = COL_ZIP_CODE + " Like '" + zipCityText + "%'";
			sortOrder = SORT_ORDER_ZIP_CODE;
		} else {
			// will not match anything ... so the list view will be empty.
			selection = COL_ID + " = ?";
			selectionArgs = new String[] { String.valueOf(-1) };
		}

		sortOrder = sortOrder + " LIMIT 50";
		// if (selection != null) {
		try {
			cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
		} catch (Exception e) {
			MyLog.e("ArticlesTable", "Exception error in getZipCityList:");
			e.printStackTrace();
		}
		// }
		return cursorLoader;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Update Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static int setActiveStationNumber(Context context, long zipCodeID, int stationNumber) {
		int numberOfUpdatedRecords = -1;
		Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(zipCodeID));
		String selection = null;
		String[] selectionArgs = null;

		ContentValues newFieldValues = new ContentValues();
		newFieldValues.put(COL_ACTIVE_STATION_NUMBER, stationNumber);

		ContentResolver cr = context.getContentResolver();
		numberOfUpdatedRecords = cr.update(uri, newFieldValues, selection, selectionArgs);

		return numberOfUpdatedRecords;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Delete Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////

}
