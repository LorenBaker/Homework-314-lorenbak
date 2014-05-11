package com.lbconsulting.homework_314_lorenbak.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lbconsulting.homework_314_lorenbak.R;
import com.lbconsulting.homework_314_lorenbak.database.WeatherStation;

public class SelectStationArrayAdaptor extends ArrayAdapter<WeatherStation> {

	private Context context;
	private int layoutResourceId;
	private WeatherStation data[] = null;

	public SelectStationArrayAdaptor(Context context, int layoutResourceId, WeatherStation[] data) {

		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		WeatherStation weatherStation = data[position];
		View row = convertView;

		if (row == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutResourceId, parent, false);
		}

		TextView tvSelectStation = (TextView) row.findViewById(R.id.tvSelectStation);
		tvSelectStation.setText(weatherStation.toString());
		tvSelectStation.setTag(weatherStation.getStationPosition());

		return row;
	}

}
