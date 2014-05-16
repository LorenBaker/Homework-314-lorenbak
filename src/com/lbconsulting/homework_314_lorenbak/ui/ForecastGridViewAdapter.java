package com.lbconsulting.homework_314_lorenbak.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lbconsulting.homework_314_lorenbak.R;

public class ForecastGridViewAdapter extends ArrayAdapter<WeatherForecastItem> {

	Context context;
	int layoutResourceID;
	ArrayList<WeatherForecastItem> weatherForecastData;

	public ForecastGridViewAdapter(Context context, int layoutResourceID,
			ArrayList<WeatherForecastItem> weatherForecastData) {
		super(context, layoutResourceID, weatherForecastData);
		this.context = context;
		this.layoutResourceID = layoutResourceID;
		this.weatherForecastData = weatherForecastData;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View cell = convertView;
		RecordHolder holder = null;

		if (cell == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			cell = inflater.inflate(layoutResourceID, parent, false);

			holder = new RecordHolder();
			holder.tvDayName = (TextView) cell.findViewById(R.id.tvDayName);
			holder.tvTemperature = (TextView) cell.findViewById(R.id.tvTemperature);
			holder.tvWeatherConditions = (TextView) cell.findViewById(R.id.tvWeatherConditions);
			holder.imageWeather = (ImageView) cell.findViewById(R.id.imageWeather);
			cell.setTag(holder);
		} else {
			holder = (RecordHolder) cell.getTag();
		}

		if (weatherForecastData != null && !weatherForecastData.isEmpty()) {
			WeatherForecastItem item = weatherForecastData.get(position);
			holder.tvDayName.setText(item.getDayName());
			holder.tvTemperature.setText(item.getTemperature());
			holder.tvWeatherConditions.setText(item.getWeatherConditions());
			// holder.tvWeatherConditions.setText("");
			holder.imageWeather.setImageBitmap(item.getWeatherBitmap());
		} else {
			holder.tvDayName.setText("DayName:" + position);
			holder.tvTemperature.setText("Temperature:" + position);
			holder.tvWeatherConditions.setText("WeatherConditions:" + position);
		}
		return cell;
	}

	public void setWeatherForecastData(ArrayList<WeatherForecastItem> weatherForecastData) {
		this.weatherForecastData = weatherForecastData;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		// return super.getCount();
		return 6;
	}

	static class RecordHolder {

		TextView tvDayName;
		TextView tvTemperature;
		TextView tvWeatherConditions;
		ImageView imageWeather;

	}
}
