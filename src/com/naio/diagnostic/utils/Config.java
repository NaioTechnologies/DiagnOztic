package com.naio.diagnostic.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Config {

	public static final String HOST_FINAL = "192.168.42.1";// "10.42.0.1";//debian/*"192.168.1.110";//joan*///"192.168.1.149";//moi//
	public static final String HOST2_FINAL = "192.168.42.1";

	public static String HOST = "192.168.42.1";
	public static String HOST2 = "192.168.42.1";

	public static final int PORT_GPS_FINAL = 3334;
	public static final int PORT_LIDAR_FINAL = 3337;
	public static final int PORT_WATCHDOG_FINAL = 0002;
	public static final int PORT_CAMERA_FINAL = 0005;
	public static final int PORT_LOG_FINAL = 3332;
	public static final int PORT_MOTORS_FINAL = 3331;
	public static final int PORT_REMOTE_FINAL = 3338;
	public static final int PORT_ACCELERO_FINAL = 3334;
	public static final int PORT_MAGNETO_FINAL = 3337;
	public static final int PORT_GYRO_FINAL = 3331;
	public static final int PORT_ODO_FINAL = 3335;
	public static final int PORT_ACTUATOR_FINAL = 3345;
	public static final int PORT_SCREEN_FINAL = 0000;
	public static final int PORT_LED_FINAL = 0001;
	public static final int PORT_KEYPAD_FINAL = 0003;
	public static final int PORT_SPEAKER_FINAL = 0004;

	public static int PORT_GPS = 3334;
	public static int PORT_LIDAR = 3337;
	public static int PORT_WATCHDOG = 0002;
	public static int PORT_CAMERA = 0005;
	public static int PORT_LOG = 3332;
	public static int PORT_MOTORS = 3331;
	public static int PORT_REMOTE = 3338;
	public static int PORT_ACCELERO = 3334;
	public static int PORT_MAGNETO = 3337;
	public static int PORT_GYRO = 3331;
	public static int PORT_ODO = 3335;
	public static int PORT_ACTUATOR = 3345;
	public static int PORT_SCREEN = 0000;
	public static int PORT_LED = 0001;
	public static int PORT_KEYPAD = 0003;
	public static int PORT_SPEAKER = 0004;

	public static final int LENGHT_HEADER = 6;
	public static final int LENGHT_ID = 1;
	public static final int LENGHT_SIZE = 4;
	public static final int LENGHT_FULL_HEADER = 11;
	public static final int LENGHT_CHECKSUM = 4;

	public static final int LENGHT_TRAME_MOTORS = 2;
	public static final int LENGHT_TRAME_LOG = 752 * 480 * 3 + 32;
	public static final int LENGHT_TRAME_WATCHDOG = 32;
	public static final int LENGHT_TRAME_GPS = 43;
	public static final int LENGHT_TRAME_ODO = 4;
	public static final int LENGHT_TRAME_CAMERA = 752 * 480 * 3 + 32;
	public static final int LENGHT_TRAME_LIDAR = 813;
	public static final int LENGHT_TRAME_REMOTE = 14;
	public static final int LENGHT_TRAME_KEYPAD = 1;
	public static final int LENGHT_TRAME_LED = 1;
	public static final int LENGHT_TRAME_SCREEN = 32;
	public static final int LENGHT_TRAME_SPEAKER = 6;
	public static final int LENGHT_TRAME_ACCELERO = 6;
	public static final int LENGHT_TRAME_ACTUATOR = 1;
	public static final int LENGHT_TRAME_GYRO = 6;
	public static final int LENGHT_TRAME_MAGNETO = 6;

	public static final int ID_MOTORS = 1;
	public static final int ID_LOG = 2;
	public static final int ID_WATCHDOG = 3;
	public static final int ID_GPS = 4;
	public static final int ID_ODO = 5;
	public static final int ID_CAMERA = 6;
	public static final int ID_LIDAR = 7;
	public static final int ID_REMOTE = 8;
	public static final int ID_ACCELERO = 9;
	public static final int ID_GYRO = 10;
	public static final int ID_MAGNETO = 11;
	public static final int ID_KEYPAD = 12;
	public static final int ID_SCREEN = 13;
	public static final int ID_SPEAKER = 14;
	public static final int ID_ACTUATOR = 15;
	public static final int ID_LED = 16;
	public static final int ID_SMS = 17;
	public static final int ID_ODO_PACKET = 18;
	public static final int ID_LIDAR_PACKET = 19;
	public static final int ID_STRING_PACKET = 20;
	public static final int ID_GPS_PACKET = 21;

	public static final int BUFFER_SIZE = 4096;

	public static final String FILE_SAVE_GPS = "bilan.naio";

	public static final int LINES_SIZE_IN_BYTES = 16;
	public static final int ID_BYTES_FOR_LOG = 2;
	public static final int BYTES_SIZE_W_H = 4;
	public static final int BYTES_SIZE_W_H_D = 6;
	public static final int POINTS2D_SIZE_IN_BYTES = 8;
	public static final int POINTS3D_SIZE_IN_BYTES = 12;

	public static final boolean ALL_DISPLAY_LIDAR_FINAL = false;
	public static final boolean ALL_DISPLAY_CAMERA_FINAL = false;
	
	public static boolean ALL_DISPLAY_LIDAR = false;
	public static boolean ALL_DISPLAY_CAMERA = false;

	public static void setPreferencesSettings(Context context) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		HOST = sharedPref.getString("ip_socket", HOST_FINAL);
		HOST2 = HOST;
		if (sharedPref.getBoolean("developer_mode", false)) {
			ALL_DISPLAY_LIDAR = sharedPref.getBoolean("all_info_lidar",ALL_DISPLAY_LIDAR_FINAL);
			ALL_DISPLAY_CAMERA = sharedPref.getBoolean("all_info_camera",ALL_DISPLAY_CAMERA_FINAL);
			PORT_LIDAR = Integer.parseInt(sharedPref.getString("port_lidar", ""+ PORT_LIDAR_FINAL));
			PORT_MOTORS = Integer.parseInt(sharedPref.getString("port_motors","" + PORT_MOTORS_FINAL));
			PORT_LOG = Integer.parseInt(sharedPref.getString("port_log", ""+ PORT_LOG_FINAL));
			PORT_GPS = Integer.parseInt(sharedPref.getString("port_gps", ""+ PORT_GPS_FINAL));
			PORT_ACTUATOR = Integer.parseInt(sharedPref.getString("port_actuator", "" + PORT_ACTUATOR_FINAL));
		}
	}

	public static void restoreDefaultPreferences(Context context) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPref.edit().putString("ip_socket", HOST_FINAL).commit();
		sharedPref.edit().putBoolean("developer_mode", false).commit();
		sharedPref.edit().putBoolean("all_info_lidar", false).commit();
		sharedPref.edit().putBoolean("all_info_camera", false).commit();
		sharedPref.edit().putString("port_lidar", "" + PORT_LIDAR_FINAL).commit();
		sharedPref.edit().putString("port_motors", "" + PORT_MOTORS_FINAL).commit();
		sharedPref.edit().putString("port_log", "" + PORT_LOG_FINAL).commit();
		sharedPref.edit().putString("port_gps", "" + PORT_GPS_FINAL).commit();
		sharedPref.edit().putString("port_actuator", "" + PORT_ACTUATOR_FINAL).commit();
	}

}
