package com.naio.diagnostic.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Config {

	public static   String HOST = "192.168.1.149";//"10.42.0.1";//debian/*"192.168.1.110";//joan*///"192.168.1.149";//moi//
	public static   String HOST2 = "192.168.1.149";

	public static   int PORT_GPS = 3334;
	public static   int PORT_LIDAR = 3337;
	public static   int PORT_WATCHDOG = 0002;
	public static   int PORT_CAMERA = 0005;
	public static   int PORT_LOG = 3332;
	public static   int PORT_MOTORS = 3331;
	public static   int PORT_REMOTE = 3338;
	public static   int PORT_ACCELERO = 3334;
	public static   int PORT_MAGNETO = 3337;
	public static   int PORT_GYRO = 3331;
	public static   int PORT_ODO = 3335;
	public static   int PORT_ACTUATOR = 3345;
	public static   int PORT_SCREEN = 0000;
	public static   int PORT_LED = 0001;
	public static   int PORT_KEYPAD = 0003;
	public static   int PORT_SPEAKER = 0004;

	public static   int LENGHT_HEADER = 6;
	public static   int LENGHT_ID = 1;
	public static   int LENGHT_SIZE = 4;
	public static   int LENGHT_FULL_HEADER = 11;
	public static   int LENGHT_CHECKSUM = 4;

	public static   int LENGHT_TRAME_MOTORS = 2;
	public static   int LENGHT_TRAME_LOG = 752*480*3+32;
	public static   int LENGHT_TRAME_WATCHDOG = 32;
	public static   int LENGHT_TRAME_GPS = 43;
	public static   int LENGHT_TRAME_ODO = 4;
	public static   int LENGHT_TRAME_CAMERA = 752*480*3+32;
	public static   int LENGHT_TRAME_LIDAR = 813;
	public static   int LENGHT_TRAME_REMOTE = 14;
	public static   int LENGHT_TRAME_KEYPAD = 1;
	public static   int LENGHT_TRAME_LED = 1;
	public static   int LENGHT_TRAME_SCREEN = 32;
	public static   int LENGHT_TRAME_SPEAKER=6;
	public static   int LENGHT_TRAME_ACCELERO = 6;
	public static   int LENGHT_TRAME_ACTUATOR = 1;
	public static   int LENGHT_TRAME_GYRO = 6;
	public static   int LENGHT_TRAME_MAGNETO = 6;


	public static final  int ID_MOTORS = 1;
	public static  final int ID_LOG = 2;
	public static  final int ID_WATCHDOG = 3;
	public static  final int ID_GPS = 4;
	public static  final int ID_ODO = 5;
	public static  final int ID_CAMERA = 6;
	public static  final int ID_LIDAR = 7;
	public static  final int ID_REMOTE = 8;
	public static  final int ID_ACCELERO = 9;
	public static  final int ID_GYRO = 10;
	public static final  int ID_MAGNETO = 11;
	public static final  int ID_KEYPAD = 12;
	public static  final int ID_SCREEN = 13;
	public static final  int ID_SPEAKER = 14;
	public static  final int ID_ACTUATOR = 15;
	public static final  int ID_LED = 16;
	public static  final int ID_SMS = 17;
	public static  final int ID_ODO_PACKET = 18;
	public static  final int ID_LIDAR_PACKET = 19;

	public static   int BUFFER_SIZE = 1024*1024;
	
	public static   String FILE_SAVE_GPS = "bilan.naio";

	public static   int LINES_SIZE_IN_BYTES = 16;
	public static   int ID_BYTES_FOR_LOG = 2;
	public static   int BYTES_SIZE_W_H = 4;
	public static   int BYTES_SIZE_W_H_D = 6;
	public static   int POINTS2D_SIZE_IN_BYTES = 8;
	public static   int POINTS3D_SIZE_IN_BYTES = 12;
	
	public static boolean ALL_DISPLAY_LIDAR = false;
	public static boolean ALL_DISPLAY_CAMERA = false;
	
	public static void setPreferencesSettings(Context context){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		HOST = sharedPref.getString("ip_socket", HOST);
		HOST2=HOST;
		if(sharedPref.getBoolean("developer_mode", false)){
			ALL_DISPLAY_LIDAR = sharedPref.getBoolean("all_info_lidar",ALL_DISPLAY_LIDAR);
			ALL_DISPLAY_CAMERA = sharedPref.getBoolean("all_info_camera",ALL_DISPLAY_CAMERA);
			/*ID_MOTORS = Integer.parseInt(sharedPref.getString("id_motors", ""+ID_MOTORS));
			ID_LOG = Integer.parseInt(sharedPref.getString("id_log", ""+ID_LOG));
			ID_GPS = Integer.parseInt(sharedPref.getString("id_gps", ""+ID_GPS));
			ID_ACTUATOR = Integer.parseInt(sharedPref.getString("id_actuator", ""+ID_ACTUATOR));
			ID_ODO_PACKET = Integer.parseInt(sharedPref.getString("id_odo_packet", ""+ID_ODO_PACKET));
			ID_LIDAR_PACKET = Integer.parseInt(sharedPref.getString("id_lidar_packet", ""+ID_LIDAR_PACKET));*/
			PORT_LIDAR = Integer.parseInt(sharedPref.getString("port_lidar", ""+PORT_LIDAR));
			PORT_MOTORS= Integer.parseInt(sharedPref.getString("port_motors", ""+PORT_MOTORS));
			PORT_LOG = Integer.parseInt(sharedPref.getString("port_log", ""+PORT_LOG));
			PORT_GPS = Integer.parseInt(sharedPref.getString("port_gps", ""+PORT_GPS));
			PORT_ACTUATOR = Integer.parseInt(sharedPref.getString("port_actuator", ""+PORT_ACTUATOR));
		}
	}

	public static void restoreDefaultPreferences(Context context) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPref.edit().putString("ip_socket", HOST).commit();
		sharedPref.edit().putBoolean("developer_mode", false).commit();
		sharedPref.edit().putBoolean("all_info_lidar", false).commit();
		sharedPref.edit().putBoolean("all_info_camera", false).commit();
		
		/*sharedPref.edit().putString("id_motors", ""+ID_MOTORS).commit();
		sharedPref.edit().putString("id_log", ""+ID_LOG).commit();
		sharedPref.edit().putString("id_gps", ""+ID_GPS).commit();
		sharedPref.edit().putString("id_actuator", ""+ID_ACTUATOR).commit();
		sharedPref.edit().putString("id_odo_packet", ""+ID_ODO_PACKET).commit();
		sharedPref.edit().putString("id_lidar_packet", ""+ID_LIDAR_PACKET).commit();*/
		
		sharedPref.edit().putString("port_lidar", ""+PORT_LIDAR).commit();
		sharedPref.edit().putString("port_motors", ""+PORT_MOTORS).commit();
		sharedPref.edit().putString("port_log", ""+PORT_LOG).commit();
		sharedPref.edit().putString("port_gps", ""+PORT_GPS).commit();
		sharedPref.edit().putString("port_actuator", ""+PORT_ACTUATOR).commit();
	}
	

}
