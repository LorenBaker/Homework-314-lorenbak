package com.lbconsulting.homework_314_lorenbak.ui;

import android.app.Activity;
import android.os.Bundle;

import com.lbconsulting.homework_314_lorenbak.misc.SettingsFragment;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		// Display the Settings Fragment as the main content.
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment())
				.commit();
	}
}
