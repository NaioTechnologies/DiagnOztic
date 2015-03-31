package com.naio.diagnostic.activities;

import com.naio.diagnostic.R;
import com.naio.diagnostic.utils.SettingsFragment;

import android.app.Activity;
import android.os.Bundle;

public class PreferencesActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.form));
		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment()).commit();

	}
}
