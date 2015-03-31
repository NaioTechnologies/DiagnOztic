package com.naio.diagnostic.utils;

import com.naio.diagnostic.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment {
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		EditTextPreference hostIp = (EditTextPreference) findPreference("ip_socket");
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		hostIp.setSummary(sharedPref.getString("ip_socket", Config.HOST));
		sharedPref
				.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {

					@Override
					public void onSharedPreferenceChanged(
							SharedPreferences sharedPreferences, String key) {
						if (key.contains("ip_socket")) {
							EditTextPreference hostIp = (EditTextPreference) findPreference(key);
							hostIp.setSummary(sharedPreferences.getString(key,
									Config.HOST));
						}

					}
				});
		Preference button = (Preference) findPreference("button");
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				Config.restoreDefaultPreferences(getActivity());
				setPreferenceScreen(null);
				onCreate(savedInstanceState);
				return true;
			}
		});
		/*Preference packetId = (Preference) findPreference("screen_id");
		packetId.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				getFragmentManager()
						.beginTransaction()
						.replace(android.R.id.content,
								new SettingsPacketIdFragment()).commit();
				return true;
			}
		});*/
		
		Preference packetPorts = (Preference) findPreference("screen_port");
		packetPorts.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				getFragmentManager()
						.beginTransaction()
						.replace(android.R.id.content,
								new SettingsPortsFragment()).commit();
				return true;
			}
		});
	}

}
