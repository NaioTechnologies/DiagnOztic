package com.naio.diagnostic.packet;

import java.nio.ByteBuffer;
import java.util.Arrays;

import android.util.Log;

import com.naio.diagnostic.trames.LigneTrame;
import com.naio.diagnostic.trames.PointTrame;
import com.naio.diagnostic.trames.StringTrame;
import com.naio.diagnostic.utils.Config;

public class LidarPacket extends BasePacket{

	private PointTrame pointtrame= null;
	private LigneTrame linetrame = null;
	private StringTrame stringtrame = null;

	public LidarPacket(byte[] data) {
		super(data);
		int offset = Config.LENGHT_FULL_HEADER;
		while (offset < data.length - Config.LENGHT_FULL_HEADER) {
			Log.e("offset",""+offset+"----"+data[offset]+"--"+data[offset+1]+"----"+(data.length - Config.LENGHT_CHECKSUM - Config.LENGHT_FULL_HEADER));
			switch (data[offset]) {
			case POINTS:
				offset++;
				int nbrPoints = ByteBuffer.wrap(
						new byte[]{
								data[offset+2],
								data[offset+3],
								data[offset+4],
								data[offset+5]}
						).getInt(0);
				pointtrame = new PointTrame(data,offset);
				offset+=nbrPoints*Config.POINTS3D_SIZE_IN_BYTES + 6;
				break;
			case LINES:
				offset++;
				int nbrLines = ByteBuffer.wrap(
						new byte[]{
								data[offset+2],
								data[offset+3],
								data[offset+4],
								data[offset+5]}
						).getInt(0);
				linetrame = new LigneTrame(data,offset);
				offset+=nbrLines*Config.POINTS3D_SIZE_IN_BYTES*2 + 6;
				break;
			case STRING:
				offset++;
				int sizeString =  ByteBuffer.wrap(
						new byte[] { 
								data[offset], 
								data[offset + 1],
								data[offset + 2], 
								data[offset+3] }
						).getInt(0);
				offset+=4;
				stringtrame = new StringTrame(Arrays.copyOfRange(data,offset,offset+sizeString));
				offset+=sizeString;
				break;
			case TRIANGLES:
				offset++;
				int nbrTriangles =  ByteBuffer.wrap(
						new byte[] { 
								data[offset],
								data[offset + 1],
								data[offset + 2],
								data[offset+3] }
						).getInt(0);
				offset+=4;
				stringtrame = new StringTrame(Arrays.copyOfRange(data,offset,offset+nbrTriangles*Config.POINTS3D_SIZE_IN_BYTES*3 + 6));
				offset+=nbrTriangles*Config.POINTS3D_SIZE_IN_BYTES*3 + 6;
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
	 * @return the linetrame
	 */
	public LigneTrame getLinetrame() {
		return linetrame;
	}

	/**
	 * @return the stringtrame
	 */
	public StringTrame getStringtrame() {
		return stringtrame;
	}

}
