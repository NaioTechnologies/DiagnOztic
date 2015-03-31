package com.naio.diagnostic.utils;

import com.naio.diagnostic.R;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsPortsFragment extends PreferenceFragment {
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences_ports);

		EditTextPreference portlog = (EditTextPreference) findPreference("port_log");
		EditTextPreference portLidar = (EditTextPreference) findPreference("port_lidar");
		EditTextPreference portMotors = (EditTextPreference) findPreference("port_motors");
		EditTextPreference portGps = (EditTextPreference) findPreference("port_gps");
		EditTextPreference portActuator = (EditTextPreference) findPreference("port_actuator");

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		portlog.setSummary(sharedPref.getString("port_log", ""
				+ Config.PORT_LOG));
		portMotors.setSummary(sharedPref.getString("port_motors", ""
				+ Config.PORT_MOTORS));
		portLidar.setSummary(sharedPref.getString("port_lidar", ""
				+ Config.PORT_LIDAR));
		portGps.setSummary(sharedPref.getString("port_gps", ""
				+ Config.PORT_GPS));
		portActuator.setSummary(sharedPref.getString("port_actuator", ""
				+ Config.PORT_ACTUATOR));

		sharedPref
				.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {

					@Override
					public void onSharedPreferenceChanged(
							SharedPreferences sharedPreferences, String key) {
						if (key.contains("port")) {
							EditTextPreference hostIp = (EditTextPreference) findPreference(key);
							hostIp.setSummary(sharedPreferences.getString(key,
									""));
						}

					}
				});
		
		Preference packetPorts = (Preference) findPreference("back_ports");
		packetPorts.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				getFragmentManager()
						.beginTransaction()
						.replace(android.R.id.content,
								new SettingsFragment()).commit();
				return true;
			}
		});
	}
}
