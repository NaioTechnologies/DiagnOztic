package com.naio.diagnostic.trames;

import android.util.Log;

import com.naio.diagnostic.packet.GPSPacket;
import com.naio.diagnostic.packet.LidarPacket;
import com.naio.diagnostic.packet.OdometryPacket;
import com.naio.diagnostic.packet.StringPacket;
import com.naio.diagnostic.utils.Config;

public class TrameDecoder {
	
	private LogTrame logTrame;
	private OdometryPacket odometryPacket;
	private LidarPacket lidarPacket;
	private GPSPacket gpsPacket;
	private StringPacket stringPacket;

	public TrameDecoder(){
		super();
		logTrame = new LogTrame();
		odometryPacket = new OdometryPacket();
		lidarPacket = new LidarPacket();
		gpsPacket = new GPSPacket();
		stringPacket = new StringPacket();
	}

	public Trame decode(byte[] pollFifo) {
		
		if(pollFifo == null){
			return null;
		}
		switch (pollFifo[Config.LENGHT_HEADER]) {
		case Config.ID_GPS:
			return new GPSTrame(pollFifo);
		case Config.ID_LIDAR:
			return new LidarTrame(pollFifo);
		case Config.ID_ACCELERO:
			return new AcceleroTrame(pollFifo);
		case Config.ID_ACTUATOR:
			return new ActuatorTrame(pollFifo);
		case Config.ID_GYRO:
			return new GyroTrame(pollFifo);
		case Config.ID_LOG:
			return  logTrame.setBytes(pollFifo);
		case Config.ID_ODO:
			return new OdoTrame(pollFifo);
		case Config.ID_MOTORS:
			break;
		case Config.ID_REMOTE :
			return new RemoteTrame(pollFifo);
		case Config.ID_SPEAKER :
			return new SpeakerTrame(pollFifo);
		case Config.ID_SCREEN :
			return new ScreenTrame(pollFifo);
		case Config.ID_MAGNETO :
			return new MagnetoTrame(pollFifo);
		case Config.ID_WATCHDOG :
			return new WatchdogTrame(pollFifo);
		case Config.ID_ODO_PACKET :
			return odometryPacket.setBytes(pollFifo);
		case Config.ID_LIDAR_PACKET :
			return lidarPacket.setBytes(pollFifo);
		case Config.ID_STRING_PACKET :
			return stringPacket.setBytes(pollFifo);
		case Config.ID_GPS_PACKET :
			return gpsPacket.setBytes(pollFifo);
		default:
			break;
		}
		
		return null;
				
	}

	/**
	 * @return the logTrame
	 */
	public LogTrame getLogTrame() {
		return logTrame;
	}

	/**
	 * @return the odometryPacket
	 */
	public OdometryPacket getOdometryPacket() {
		return odometryPacket;
	}

	/**
	 * @return the lidarPacket
	 */
	public LidarPacket getLidarPacket() {
		return lidarPacket;
	}

	/**
	 * @return the gpsPacket
	 */
	public GPSPacket getGpsPacket() {
		return gpsPacket;
	}

	/**
	 * @return the stringPacket
	 */
	public StringPacket getStringPacket() {
		return stringPacket;
	}
	
	

}
