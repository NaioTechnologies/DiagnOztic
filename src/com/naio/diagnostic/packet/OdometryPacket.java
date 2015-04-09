package com.naio.diagnostic.packet;

import java.nio.ByteBuffer;
import java.util.Arrays;

import android.util.Log;

import com.naio.diagnostic.trames.JPEGTrame;
import com.naio.diagnostic.trames.LigneTrame;
import com.naio.diagnostic.trames.PointTrame;
import com.naio.diagnostic.trames.StringTrame;
import com.naio.diagnostic.trames.Trame;
import com.naio.diagnostic.utils.Config;

public class OdometryPacket extends BasePacket {

	private PointTrame pointtrame = null;
	private JPEGTrame jpegtrame = null;
	private StringTrame stringtrame = null;
	private LigneTrame linetrame = null;

	public OdometryPacket(byte[] data) {
		super(data);
		setBytes(data);
	}

	public OdometryPacket() {
		super();
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

	/**
	 * @return the stringtrame
	 */
	public StringTrame getStringtrame() {
		return stringtrame;
	}

	/**
	 * @return the linetrame
	 */
	public LigneTrame getLinetrame() {
		return linetrame;
	}

	public Trame setBytes(byte[] data) {
		int offset = Config.LENGHT_FULL_HEADER;
		while (offset < data.length - Config.LENGHT_FULL_HEADER) {
			Log.e("offset",	""+ offset+ "----"+ data[offset]+ "--"+ data[offset + 1]+ "----"+ (data.length - Config.LENGHT_FULL_HEADER));
			switch (data[offset]) {
			case JPEG:
				offset++;
				int sizeJpeg = ByteBuffer.wrap(
						new byte[] { 
								data[offset],
								data[offset + 1],
								data[offset + 2],
								data[offset + 3] }
						).getInt(0);
				Log.e("sizejpeg", "" + sizeJpeg);
				offset += 4;
				if (jpegtrame == null)
					jpegtrame = new JPEGTrame(Arrays.copyOfRange(data, offset,offset + sizeJpeg));
				else
					jpegtrame.setBytes(Arrays.copyOfRange(data, offset, offset+ sizeJpeg));
				offset += sizeJpeg;
				break;
			case POINTS:
				offset++;
				int nbrPoints = ByteBuffer.wrap(
						new byte[] { 
								data[offset + 2],
								data[offset + 3],
								data[offset + 4],
								data[offset + 5] }
						).getInt(0);
				Log.e("offsetpoint", "" + nbrPoints);
				if(nbrPoints > 1000)
					return this;
				if (pointtrame == null)
					pointtrame = new PointTrame(data, offset);
				else
					pointtrame.setBytes(data, offset);
				offset += nbrPoints * Config.POINTS3D_SIZE_IN_BYTES + 6;
				break;
			case LINES:
				offset++;
				int nbrLines = ByteBuffer.wrap(
						new byte[] { 
								data[offset + 2],
								data[offset + 3],
								data[offset + 4],
								data[offset + 5] }
						).getInt(0);
				Log.e("offsetline", "" + nbrLines);
				if(nbrLines > 1000)
					return this;
				if (linetrame == null)
					linetrame = new LigneTrame(data, offset);
				else
					linetrame.setBytes(data, offset);
				offset += nbrLines * Config.POINTS3D_SIZE_IN_BYTES * 2 + 6;
				break;
			case STRING:
				offset++;
				int sizeString = ByteBuffer.wrap(
						new byte[] { 
								data[offset], 
								data[offset + 1],
								data[offset + 2],
								data[offset + 3] }
						).getInt(0);
				offset += 4;
				Log.e("stringtrame", "" + sizeString + "---");
				if(sizeString > 200 || sizeString <2)
					return this;
				stringtrame = new StringTrame(Arrays.copyOfRange(data, offset,offset + sizeString));
				offset += sizeString;
				break;
			default:
				return this;
			}

		}
		return this;
	}

}
