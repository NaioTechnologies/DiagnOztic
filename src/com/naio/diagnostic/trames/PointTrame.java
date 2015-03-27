package com.naio.diagnostic.trames;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import android.util.Log;

import com.naio.diagnostic.utils.Config;

public class PointTrame extends Trame {

	private byte type; // 0 ou 1 ( int or float )
	private int nbrPoints;
	private ArrayList<float[]> arrayListPoints3DFloat = null;
	private ArrayList<int[]> arrayListPoints3DInt = null;
	private byte dimension;
	public final static Object lock = new Object();

	public PointTrame(byte[] data) {
		super(data);
		int offset = 0;
		type = data[offset++];//float32
		dimension = data[offset++]; //2d,3d
		nbrPoints = ByteBuffer.wrap(new byte[]{data[offset],data[offset+1],data[offset+2],data[offset+3]}).getInt(0);
		offset+=4;
		synchronized (lock) {

			if (type == 0x0) {
				arrayListPoints3DInt = new ArrayList<int[]>();
			} else if (type == 0x9) {
				arrayListPoints3DFloat = new ArrayList<float[]>();
			}
			
			if (data.length == 6 + nbrPoints * 4*3) {
				for (int i = 0; i < nbrPoints; i++) {
					byte[] point3d_x = new byte[] { data[offset + 3],
							data[offset + 2], data[offset + 1], data[offset] };
					byte[] point3d_y = new byte[] { data[offset + 7],
							data[offset + 6], data[offset + 5],
							data[offset + 4] };
					byte[] point3d_z = new byte[] { data[offset + 11],
							data[offset + 10], data[offset + 9],
							data[offset + 8] };
					offset = offset + Config.POINTS3D_SIZE_IN_BYTES;
					if (type == 0) {
						int[] point3d = new int[3];
						point3d[0] = ByteBuffer.wrap(point3d_x).getInt(0);
						point3d[1] = ByteBuffer.wrap(point3d_y).getInt(0);
						point3d[2] = ByteBuffer.wrap(point3d_z).getInt(0);
						arrayListPoints3DInt.add(point3d);
					} else if (type == 0x9) {
						float[] point3d = new float[3];
						point3d[0] = ByteBuffer.wrap(point3d_x).getFloat(0);
						point3d[1] = ByteBuffer.wrap(point3d_y).getFloat(0);
						point3d[2] = ByteBuffer.wrap(point3d_z).getFloat(0);
						arrayListPoints3DFloat.add(point3d);
					}
				}
			}
		}
	}

	/**
	 * @return the arrayListPoints3DFloat
	 */
	public ArrayList<float[]> getArrayListPoints3DFloat() {
		synchronized (lock) {

			return arrayListPoints3DFloat;
		}
	}

	/**
	 * @return the arrayListPoints3DInt
	 */
	public ArrayList<int[]> getArrayListPoints3DInt() {
		return arrayListPoints3DInt;
	}

}
