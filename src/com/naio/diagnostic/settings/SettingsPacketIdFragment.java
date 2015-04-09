package com.naio.diagnostic.settings;

import com.naio.diagnostic.R;
import com.naio.diagnostic.utils.Config;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsPacketIdFragment extends PreferenceFragment {
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences_packet_id);

		EditTextPreference idMotors = (EditTextPreference) findPreference("id_motors");
		EditTextPreference idlog = (EditTextPreference) findPreference("id_log");
		EditTextPreference idGps = (EditTextPreference) findPreference("id_gps");
		EditTextPreference idActuator = (EditTextPreference) findPreference("id_actuator");
		EditTextPreference idOdoPacket = (EditTextPreference) findPreference("id_odo_packet");
		EditTextPreference idLidarPacket = (EditTextPreference) findPreference("id_lidar_packet");
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

		idMotors.setSummary(sharedPref.getString("id_motors", ""+ Config.ID_MOTORS));
		idlog.setSummary(sharedPref.getString("id_log", "" + Config.ID_LOG));
		idGps.setSummary(sharedPref.getString("id_gps", "" + Config.ID_GPS));
		idActuator.setSummary(sharedPref.getString("id_actuator", ""+ Config.ID_ACTUATOR));
		idOdoPacket.setSummary(sharedPref.getString("id_odo_packet", ""+ Config.ID_ODO_PACKET));
		idLidarPacket.setSummary(sharedPref.getString("id_lidar_packet", ""	+ Config.ID_LIDAR_PACKET));

		sharedPref.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {

					@Override
					public void onSharedPreferenceChanged(
							SharedPreferences sharedPreferences, String key) {
						if (key.contains("id")) {
							EditTextPreference hostIp = (EditTextPreference) findPreference(key);
							hostIp.setSummary(sharedPreferences.getString(key,
									""));
						}

					}
				});

		Preference packetPorts = (Preference) findPreference("back_id");
		packetPorts
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
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
