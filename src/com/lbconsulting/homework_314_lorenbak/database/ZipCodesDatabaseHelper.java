package com.lbconsulting.homework_314_lorenbak.database;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import com.lbconsulting.homework_314_lorenbak.misc.MyLog;

public class ZipCodesDatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "zipcodes.db";
	// private static String DATABASE_PATH = "";
	private static String DATABASE_PATH = "/data/data/com.lbconsulting.homework_314_lorenbak/databases/";

	public static String ZIP_CODES_TABLE = "zipcodes_with_stations";
	public static String COL_ZIP_CODE = "ZipCode";
	public static String COL_STATION_1_XML_URL = "station1_xml_url";
	public static String COL_STATION_2_XML_URL = "station2_xml_url";
	public static String COL_STATION_3_XML_URL = "station3_xml_url";
	public static String COL_LOCATION = "Location";
	public static String COL_ID = "_id";

	private final Context myContext;

	private static SQLiteDatabase zipcodes_dBase;

	public ZipCodesDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.myContext = context;
		/*String filesDirectory = context.getFilesDir().getPath();
		DATABASE_PATH = filesDirectory + "/data/com.lbconsulting.homework_314_lorenbak/databases/";*/
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own database.
	 * */
	public void createDataBase() throws IOException {
		boolean dbExist = checkDataBase();
		if (dbExist) {
			// do nothing - database already exist
		} else {
			// By calling this method and empty database will be created into the default system path
			// of your application so we are going be able to overwrite that database with our database.
			MyLog.i("ZipCodesDatabaseHelper", "Creating zipcodes database.");
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying zipcodes database");
			}
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each time you open the application.
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {

		SQLiteDatabase checkDB = null;
		try {
			String myPath = DATABASE_PATH + DATABASE_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {
			// database does't exist yet.
		}

		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created empty database in the
	 * system folder, from where it can be accessed and handled.
	 * This is done by transferring byte stream.
	 * */
	private void copyDataBase() throws IOException {

		MyLog.i("ZipCodesDatabaseHelper", "Copying zipcodes database from assests to the applcation data folder.");
		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DATABASE_NAME);

		// Path to the just created empty db
		String outFileName = DATABASE_PATH + DATABASE_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the input file to the output file
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public void openDataBase() throws SQLException {
		// Open the database
		String myPath = DATABASE_PATH + DATABASE_NAME;
		zipcodes_dBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		if (zipcodes_dBase == null) {
			MyLog.e("ZipCodesDatabaseHelper", "Unable to open the zipcodes database!");
		} else {
			MyLog.i("ZipCodesDatabaseHelper", "Zipcodes database opened.");
		}
	}

	@Override
	public synchronized void close() {
		if (zipcodes_dBase != null)
			zipcodes_dBase.close();
		MyLog.i("ZipCodesDatabaseHelper", "Zipcodes database closed.");
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		MyLog.i("ZipCodesDatabaseHelper", "onCreate");

	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		MyLog.i("ZipCodesDatabaseHelper", "onUpgrade");

	}

	public static SQLiteDatabase getDatabase() {
		return zipcodes_dBase;
	}

	@SuppressWarnings("resource")
	public Cursor getZipCode(String zipCode) {
		Cursor zipCodeCursor = null;

		String[] projection = new String[] { "*" };
		String selection = COL_ZIP_CODE + " = " + zipCode;
		String[] selectionArgs = null;
		String sortOrder = null;
		String groupBy = null;
		String having = null;

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(ZIP_CODES_TABLE);
		queryBuilder.appendWhere(selection);

		// Execute the query on the database
		SQLiteDatabase db = null;
		try {
			db = getReadableDatabase();
		} catch (SQLiteException ex) {
			MyLog.e("ZipCodesDatabaseHelper", "SQLiteException error in getZipCode:" + zipCode);
		}

		if (null != db) {

			try {
				zipCodeCursor = queryBuilder
						.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder);
			} catch (Exception e) {
				MyLog.e("ZipCodesDatabaseHelper", "Exception error in getZipCode:" + zipCode);
				e.printStackTrace();
			}
		}
		return zipCodeCursor;
	}

	@SuppressWarnings("resource")
	public Cursor getZipCode(long zipCodeID) {
		Cursor zipCodeCursor = null;

		String[] projection = new String[] { "*" };
		String selection = COL_ID + " = " + zipCodeID;
		String[] selectionArgs = null;
		String sortOrder = null;
		String groupBy = null;
		String having = null;

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(ZIP_CODES_TABLE);
		queryBuilder.appendWhere(selection);

		// Execute the query on the database
		SQLiteDatabase db = null;
		try {
			db = getReadableDatabase();
		} catch (SQLiteException ex) {
			MyLog.e("ZipCodesDatabaseHelper", "SQLiteException error in getZipCode:" + zipCodeID);
		}

		if (null != db) {

			try {
				zipCodeCursor = queryBuilder
						.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder);
			} catch (Exception e) {
				MyLog.e("ZipCodesDatabaseHelper", "Exception error in getZipCode:" + zipCodeID);
				e.printStackTrace();
			}
		}
		return zipCodeCursor;
	}

}
