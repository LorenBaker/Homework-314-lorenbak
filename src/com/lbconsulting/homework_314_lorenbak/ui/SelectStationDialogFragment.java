package com.lbconsulting.homework_314_lorenbak.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.lbconsulting.homework_314_lorenbak.R;
import com.lbconsulting.homework_314_lorenbak.database.WeatherStation;
import com.lbconsulting.homework_314_lorenbak.database.ZipCodesTable;
import com.lbconsulting.homework_314_lorenbak.misc.MyLog;

public class SelectStationDialogFragment extends DialogFragment {

	// private Button btnApply;
	private Button btnCancel;
	private ListView select_station_listview;
	private String zipCode;
	private int active_units;

	public SelectStationDialogFragment() {
		// Empty constructor required for DialogFragment
	}

	public static SelectStationDialogFragment newInstance(String zipCode, int active_units) {

		SelectStationDialogFragment f = new SelectStationDialogFragment();
		Bundle args = new Bundle();
		args.putString("zipCode", zipCode);
		args.putInt("active_units", active_units);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onAttach(Activity activity) {
		MyLog.i("SelectStationDialogFragment", "onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Store our mActiveGroupID
		outState.putString("zipCode", this.zipCode);
		outState.putInt("active_units", this.active_units);
		super.onSaveInstanceState(outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (savedInstanceState != null && savedInstanceState.containsKey("mActiveGroupID")) {
			this.zipCode = savedInstanceState.getString("zipCode");
			this.active_units = savedInstanceState.getInt("active_units", MainActivity.US_STANDARD_UNITS);
		} else {
			Bundle bundle = getArguments();
			if (bundle != null) {
				this.zipCode = bundle.getString("zipCode");
				this.active_units = bundle.getInt("active_units", MainActivity.US_STANDARD_UNITS);
			}
		}

		View view = null;
		Cursor zipCodesCursor = ZipCodesTable.getZipCode(getActivity(), zipCode);
		if (zipCodesCursor != null) {
			zipCodesCursor.moveToFirst();

			WeatherStation[] stationsList = new WeatherStation[3];

			// Station 1
			WeatherStation station = new WeatherStation();
			station.setActive_units(active_units);

			float distanceKm = zipCodesCursor.getFloat(zipCodesCursor
					.getColumnIndexOrThrow(ZipCodesTable.COL_STATION_1_DISTANCE_KM));
			station.setStationDistance_km(distanceKm);
			float distanceMi = zipCodesCursor.getFloat(zipCodesCursor
					.getColumnIndexOrThrow(ZipCodesTable.COL_STATION_1_DISTANCE_MI));
			station.setStationDistance_mi(distanceMi);

			String stationID = zipCodesCursor.getString(zipCodesCursor
					.getColumnIndexOrThrow(ZipCodesTable.COL_STATION_1_ID));
			station.setStationID(stationID);

			String stationName = zipCodesCursor.getString(zipCodesCursor
					.getColumnIndexOrThrow(ZipCodesTable.COL_STATION_1_NAME));
			station.setStationName(stationName);

			station.setStationPosition(1);
			stationsList[0] = station;

			// Station 2
			station = new WeatherStation();
			station.setActive_units(active_units);

			distanceKm = zipCodesCursor.getFloat(zipCodesCursor
					.getColumnIndexOrThrow(ZipCodesTable.COL_STATION_2_DISTANCE_KM));
			station.setStationDistance_km(distanceKm);
			distanceMi = zipCodesCursor.getFloat(zipCodesCursor
					.getColumnIndexOrThrow(ZipCodesTable.COL_STATION_2_DISTANCE_MI));
			station.setStationDistance_mi(distanceMi);

			stationID = zipCodesCursor.getString(zipCodesCursor.getColumnIndexOrThrow(ZipCodesTable.COL_STATION_2_ID));
			station.setStationID(stationID);

			stationName = zipCodesCursor.getString(zipCodesCursor
					.getColumnIndexOrThrow(ZipCodesTable.COL_STATION_2_NAME));
			station.setStationName(stationName);

			station.setStationPosition(2);
			stationsList[1] = station;

			// Station 3
			station = new WeatherStation();
			station.setActive_units(active_units);

			distanceKm = zipCodesCursor.getFloat(zipCodesCursor
					.getColumnIndexOrThrow(ZipCodesTable.COL_STATION_3_DISTANCE_KM));
			station.setStationDistance_km(distanceKm);
			distanceMi = zipCodesCursor.getFloat(zipCodesCursor
					.getColumnIndexOrThrow(ZipCodesTable.COL_STATION_3_DISTANCE_MI));
			station.setStationDistance_mi(distanceMi);

			stationID = zipCodesCursor.getString(zipCodesCursor.getColumnIndexOrThrow(ZipCodesTable.COL_STATION_3_ID));
			station.setStationID(stationID);

			stationName = zipCodesCursor.getString(zipCodesCursor
					.getColumnIndexOrThrow(ZipCodesTable.COL_STATION_3_NAME));
			station.setStationName(stationName);

			station.setStationPosition(3);
			stationsList[2] = station;

			zipCodesCursor.close();

			getDialog().setTitle("Select New Weather Station");

			// inflate view
			view = inflater.inflate(R.layout.dialog_select_station, container);

			if (view != null) {
				/*				btnApply = (Button) view.findViewById(R.id.btnApply);
								btnApply.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {

										getDialog().dismiss();
									}
								});*/

				btnCancel = (Button) view.findViewById(R.id.btnCancel);
				btnCancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						getDialog().dismiss();
					}
				});

				select_station_listview = (ListView) view.findViewById(R.id.select_station_listview);
				select_station_listview.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View listView, int position, long id) {
						// TODO Auto-generated method stub

					}

				});

				SelectStationArrayAdaptor selectStationArrayAdaptor = new SelectStationArrayAdaptor(getActivity(),
						R.layout.row_select_station, stationsList);
				select_station_listview.setAdapter(selectStationArrayAdaptor);
			}
		}
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		MyLog.i("SelectStationDialogFragment", "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		MyLog.i("SelectStationDialogFragment", "onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		MyLog.i("SelectStationDialogFragment", "onCreateDialog");
		return super.onCreateDialog(savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		MyLog.i("SelectStationDialogFragment", "onDestroyView");
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		MyLog.i("SelectStationDialogFragment", "onDetach");
		super.onDetach();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		MyLog.i("SelectStationDialogFragment", "onDismiss");
		super.onDismiss(dialog);
	}

	@Override
	public void onStart() {
		MyLog.i("SelectStationDialogFragment", "onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		MyLog.i("SelectStationDialogFragment", "onStop");
		super.onStop();
	}

	@Override
	public void onDestroy() {
		MyLog.i("SelectStationDialogFragment", "onDestroy");
		super.onDestroy();
	}

}
