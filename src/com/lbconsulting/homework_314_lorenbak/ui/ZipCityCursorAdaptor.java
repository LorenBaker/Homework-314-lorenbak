package com.lbconsulting.homework_314_lorenbak.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lbconsulting.homework_314_lorenbak.R;
import com.lbconsulting.homework_314_lorenbak.database.ZipCodesTable;
import com.lbconsulting.homework_314_lorenbak.misc.MyLog;

public class ZipCityCursorAdaptor extends CursorAdapter {

	public ZipCityCursorAdaptor(Context context, Cursor c, int flags) {
		super(context, c, flags);
		MyLog.i("ZipCityCursorAdaptor", "ZipCityCursorAdaptor constructor.");
	}

	@Override
	public void bindView(View view, final Context context, final Cursor cursor) {
		if (cursor != null && view != null) {

			TextView tvZipCityRow = (TextView) view.findViewById(R.id.tvZipCityRow);
			if (tvZipCityRow != null) {
				tvZipCityRow
						.setText(cursor.getString(cursor.getColumnIndexOrThrow(ZipCodesTable.COL_LOCATION)));
			}
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// MyLog.i("ZipCityCursorAdaptor", "newView()");
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.row_zip_city, parent, false);
		return view;
	}

}
