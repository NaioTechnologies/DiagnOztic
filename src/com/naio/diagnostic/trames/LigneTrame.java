package com.naio.diagnostic.trames;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import android.util.Log;

import com.naio.diagnostic.utils.Config;

public class LigneTrame extends Trame {

	private byte type;
	private byte dimension;
	private int nbrLines;
	private ArrayList<float[]> arrayListLines3DFloat = null;
	private ArrayList<int[]> arrayListLines3DInt = null;

	public LigneTrame(byte[] data, int offsetParam) {
		super(data);
		int offset = offsetParam;
		type = data[offset++];// int float etc
		dimension = data[offset++];//2d 3d
		nbrLines = ByteBuffer.wrap(
				new byte[] { data[offset], data[offset + 1], data[offset + 2],
						data[offset + 3] }).getInt(0);
		offset += 4;

		if (type == 0x0) {
			arrayListLines3DInt = new ArrayList<int[]>();
		} else if (type == 0x9) {
			arrayListLines3DFloat = new ArrayList<float[]>();
		}

		if (data.length- offsetParam >= 6 + nbrLines * 4 * 3 * 2) {
			for (int i = 0; i < nbrLines; i++) {
				byte[] point3d_x = new byte[] { data[offset + 3],
						data[offset + 2], data[offset + 1], data[offset] };
				byte[] point3d_y = new byte[] { data[offset + 7],
						data[offset + 6], data[offset + 5], data[offset + 4] };
				byte[] point3d_z = new byte[] { data[offset + 11],
						data[offset + 10], data[offset + 9], data[offset + 8] };
				byte[] point3d_x_2 = new byte[] { data[offset + 15],
						data[offset + 14], data[offset + 13], data[offset+12] };
				byte[] point3d_y_2 = new byte[] { data[offset + 19],
						data[offset + 18], data[offset + 17], data[offset + 16] };
				byte[] point3d_z_2 = new byte[] { data[offset + 23],
						data[offset + 22], data[offset + 21], data[offset + 20] };
				offset = offset + Config.POINTS3D_SIZE_IN_BYTES*2;
				if (type == 0) {
					int[] point3d = new int[6];
					point3d[0] = ByteBuffer.wrap(point3d_x).getInt(0);
					point3d[1] = ByteBuffer.wrap(point3d_y).getInt(0);
					point3d[2] = ByteBuffer.wrap(point3d_z).getInt(0);
					point3d[3] = ByteBuffer.wrap(point3d_x_2).getInt(0);
					point3d[4] = ByteBuffer.wrap(point3d_y_2).getInt(0);
					point3d[5] = ByteBuffer.wrap(point3d_z_2).getInt(0);
					arrayListLines3DInt.add(point3d);
				} else if (type == 0x9) {
					float[] point3d = new float[6];
					point3d[0] = ByteBuffer.wrap(point3d_x).getFloat(0);
					point3d[1] = ByteBuffer.wrap(point3d_y).getFloat(0);
					point3d[2] = ByteBuffer.wrap(point3d_z).getFloat(0);
					point3d[3] = ByteBuffer.wrap(point3d_x_2).getFloat(0);
					point3d[4] = ByteBuffer.wrap(point3d_y_2).getFloat(0);
					point3d[5] = ByteBuffer.wrap(point3d_z_2).getFloat(0);
					arrayListLines3DFloat.add(point3d);
				}
			}
		}

	}

	/**
	 * @return the arrayListLines3DFloat
	 */
	public ArrayList<float[]> getArrayListLines3DFloat() {
		return arrayListLines3DFloat;
	}

	/**
	 * @return the arrayListLines3DInt
	 */
	public ArrayList<int[]> getArrayListLines3DInt() {
		return arrayListLines3DInt;
	}

	public void setBytes(byte[] data, int offsetParam) {
		int offset = offsetParam;
		type = data[offset++];// int float etc
		dimension = data[offset++];//2d 3d
		nbrLines = ByteBuffer.wrap(
				new byte[] { data[offset], data[offset + 1], data[offset + 2],
						data[offset + 3] }).getInt(0);
		offset += 4;

		if (type == 0x0) {
			arrayListLines3DInt = new ArrayList<int[]>();
		} else if (type == 0x9) {
			arrayListLines3DFloat = new ArrayList<float[]>();
		}
		Log.e("lignes",""+(data.length - offsetParam) + " compare to "+(6 + nbrLines * 4 * 3 * 2));
		if (data.length - offsetParam >= 6 + nbrLines * 4 * 3 * 2) {
			for (int i = 0; i < nbrLines; i++) {
				byte[] point3d_x = new byte[] { data[offset + 3],
						data[offset + 2], data[offset + 1], data[offset] };
				byte[] point3d_y = new byte[] { data[offset + 7],
						data[offset + 6], data[offset + 5], data[offset + 4] };
				byte[] point3d_z = new byte[] { data[offset + 11],
						data[offset + 10], data[offset + 9], data[offset + 8] };
				byte[] point3d_x_2 = new byte[] { data[offset + 15],
						data[offset + 14], data[offset + 13], data[offset+12] };
				byte[] point3d_y_2 = new byte[] { data[offset + 19],
						data[offset + 18], data[offset + 17], data[offset + 16] };
				byte[] point3d_z_2 = new byte[] { data[offset + 23],
						data[offset + 22], data[offset + 21], data[offset + 20] };
				offset = offset + Config.POINTS3D_SIZE_IN_BYTES*2;
				if (type == 0) {
					int[] point3d = new int[6];
					point3d[0] = ByteBuffer.wrap(point3d_x).getInt(0);
					point3d[1] = ByteBuffer.wrap(point3d_y).getInt(0);
					point3d[2] = ByteBuffer.wrap(point3d_z).getInt(0);
					point3d[3] = ByteBuffer.wrap(point3d_x_2).getInt(0);
					point3d[4] = ByteBuffer.wrap(point3d_y_2).getInt(0);
					point3d[5] = ByteBuffer.wrap(point3d_z_2).getInt(0);
					arrayListLines3DInt.add(point3d);
				} else if (type == 0x9) {
					float[] point3d = new float[6];
					point3d[0] = ByteBuffer.wrap(point3d_x).getFloat(0);
					point3d[1] = ByteBuffer.wrap(point3d_y).getFloat(0);
					point3d[2] = ByteBuffer.wrap(point3d_z).getFloat(0);
					point3d[3] = ByteBuffer.wrap(point3d_x_2).getFloat(0);
					point3d[4] = ByteBuffer.wrap(point3d_y_2).getFloat(0);
					point3d[5] = ByteBuffer.wrap(point3d_z_2).getFloat(0);
					arrayListLines3DFloat.add(point3d);
				}
			}
		}
		
	}
	
	

}
