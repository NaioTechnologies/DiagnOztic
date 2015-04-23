package com.naio.diagnostic.packet;

import java.nio.ByteBuffer;
import java.util.Arrays;

import android.util.Log;

import com.naio.diagnostic.trames.LigneTrame;
import com.naio.diagnostic.trames.PointTrame;
import com.naio.diagnostic.trames.StringTrame;
import com.naio.diagnostic.utils.Config;

public class GPSPacket extends BasePacket {

	private StringTrame stringtrame;
	private PointTrame pointtrame;

	public GPSPacket(byte[] data) {
		int offset = Config.LENGHT_FULL_HEADER;

		while (offset < data.length - Config.LENGHT_FULL_HEADER) {
			switch (data[offset]) {
			case POINTS:
				offset++;
				int nbrPoints = ByteBuffer.wrap(
						new byte[] { data[offset + 2], data[offset + 3],
								data[offset + 4], data[offset + 5] }).getInt(0);
				pointtrame = new PointTrame(data, offset);
				offset += nbrPoints * pointtrame.getSizeBytePerPoint() + 6;
				break;
			case STRING:
				offset++;
				int sizeString = ByteBuffer.wrap(
						new byte[] { data[offset], data[offset + 1],
								data[offset + 2], data[offset + 3] }).getInt(0);
				offset += 4;
				stringtrame = new StringTrame(Arrays.copyOfRange(data, offset,
						offset + sizeString));
				offset += sizeString + 1;
				break;
			}
		}
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
	
	
}
