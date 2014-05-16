package com.lbconsulting.homework_314_lorenbak.misc;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lbconsulting.homework_314_lorenbak.R;

public class SettingsFragment extends PreferenceFragment {

	public static final String KEY_DISPLAY_UNITS = "display_units";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.pref_general);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		view.setBackground(getResources().getDrawable(R.drawable.rectangle_green_gradient_black_stroke));

		return view;
	}
}
