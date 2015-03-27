package com.naio.diagnostic.packet;

import java.nio.ByteBuffer;
import java.util.Arrays;

import android.util.Log;

import com.naio.diagnostic.trames.JPEGTrame;
import com.naio.diagnostic.trames.PointTrame;
import com.naio.diagnostic.trames.Trame;
import com.naio.diagnostic.utils.Config;

public class OdometryPacket extends Trame {
	private static final byte JPEG = 0x04;
	private static final byte POINTS = 0x01;
	private PointTrame pointtrame = null;
	private JPEGTrame jpegtrame = null;

	public OdometryPacket(byte[] data) {
		super(data);
		int offset = Config.LENGHT_FULL_HEADER;
		while (offset < data.length - Config.LENGHT_CHECKSUM - Config.LENGHT_FULL_HEADER) {
			switch (data[offset]) {
			case JPEG:
				offset++;
				int sizeJpeg = ByteBuffer.wrap(
						new byte[] { data[offset], data[offset + 1],
								data[offset + 2], data[offset+3] }).getInt(0);
				offset += 4;
				jpegtrame  = new JPEGTrame(Arrays.copyOfRange(data,
						offset, offset+sizeJpeg));
				offset += sizeJpeg;
				Log.e("odoPacket","offset   "+offset);
				break;
			case POINTS:
				offset++;
				int nbrPoints = (int) data[offset+1];
				pointtrame = new PointTrame(Arrays.copyOfRange(data,offset,offset+nbrPoints*Config.POINTS3D_SIZE_IN_BYTES + 2));
				offset+=nbrPoints*Config.POINTS3D_SIZE_IN_BYTES + 2;
				break;
			default:
				return;
			}
		}
	}

	/**
	 * @return the pointtrame
	 */
	public PointTrame getPointtrame() {
		return pointtrame;
	}

	/**
	 * @return the jpegtrame
	 */
	public JPEGTrame getJpegtrame() {
		return jpegtrame;
	}
	
	

}
