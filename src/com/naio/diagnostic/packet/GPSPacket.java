package com.naio.diagnostic.packet;

import java.nio.ByteBuffer;
import java.util.Arrays;

import android.util.Log;

import com.naio.diagnostic.trames.LigneTrame;
import com.naio.diagnostic.trames.PointTrame;
import com.naio.diagnostic.trames.StringTrame;
import com.naio.diagnostic.trames.Trame;
import com.naio.diagnostic.utils.Config;

public class GPSPacket extends BasePacket {

	private StringTrame stringtrame;
	private PointTrame pointtrame;

	public GPSPacket(){
		super();
	}
	
	public GPSPacket(byte[] data) {
		super(data);
		setBytes(data);
		
	}
	
	public Trame setBytes(byte[] data){
		int offset = Config.LENGHT_FULL_HEADER;

		while (offset < data.length - Config.LENGHT_FULL_HEADER) {
			switch (data[offset]) {
			case POINTS:
				offset++;
				int nbrPoints = ByteBuffer.wrap(
						new byte[] { data[offset + 2], data[offset + 3],
								data[offset + 4], data[offset + 5] }).getInt(0);
				if (pointtrame == null)
					pointtrame = new PointTrame(data, offset);
				else
					pointtrame.setBytes(data, offset);
				offset += nbrPoints * pointtrame.getSizeBytePerPoint() + 6;
				break;
			case STRING:
				offset++;
				int sizeString = ByteBuffer.wrap(
						new byte[] { data[offset], data[offset + 1],
								data[offset + 2], data[offset + 3] }).getInt(0);
				offset += 4;
				stringtrame = new StringTrame(Arrays.copyOfRange(data, offset,
						offset + sizeString+1));
				offset += sizeString + 1;
				break;
			}
		}
		return this;
	}

	/**
	 * @return the stringtrame
	 */
	public StringTrame getStringtrame() {
		return stringtrame;
	}

	/**
	 * @return the pointtrame
	 */
	public PointTrame getPointtrame() {
		return pointtrame;
	}
	
	/**
	 * @return the lat
	 */
	public double getLat() {
		if(pointtrame == null)
			return 0.0;
		if(pointtrame.getArrayListPoints3DDouble() == null)
			return 0.0;
		if(pointtrame.getArrayListPoints3DDouble().isEmpty()){
			return 0.0;
		}
		return pointtrame.getArrayListPoints3DDouble().get(0)[0];
	}

	/**
	 * @return the lon
	 */
	public double getLon() {
		if(pointtrame == null)
			return 0.0;
		if(pointtrame.getArrayListPoints3DDouble() == null)
			return 0.0;
		if(pointtrame.getArrayListPoints3DDouble().isEmpty()){
			return 0.0;
		}
		return pointtrame.getArrayListPoints3DDouble().get(0)[1];
	}

	/**
	 * @return the alt
	 */
	public double getAlt() {
		if(pointtrame == null)
			return 0.0;
		if(pointtrame.getArrayListPoints3DDouble() == null)
			return 0.0;
		if(pointtrame.getArrayListPoints3DDouble().isEmpty()){
			return 0.0;
		}
		return pointtrame.getArrayListPoints3DDouble().get(0)[2];
	}
	
	
}
