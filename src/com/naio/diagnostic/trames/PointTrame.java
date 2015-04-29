package com.naio.diagnostic.trames;

import java.nio.ByteBuffer;
import net.sourceforge.juint.UInt16;
import java.util.ArrayList;

import android.util.Log;

import com.naio.diagnostic.packet.OdometryPacket;
import com.naio.diagnostic.utils.Config;

public class PointTrame extends Trame {

	private int nbrPoints;
	private ArrayList<float[]> arrayListPoints3DFloat = null;
	private ArrayList<int[]> arrayListPoints3DInt = null;
	private ArrayList<double[]> arrayListPoints3DDouble = null;
	private ArrayList<UInt16> arrayListPoints1DUInt16 = null;
	public final static Object lock = new Object();

	public PointTrame(byte[] data, int offsetParam) {
		super(data);
		setBytes(data, offsetParam);
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
	 * @return the arrayListPoints3DDouble
	 */
	public ArrayList<double[]> getArrayListPoints3DDouble() {
		synchronized (lock) {

			return arrayListPoints3DDouble;
		}
	}
	
	

	/**
	 * @return the arrayListPoints1DUInt16
	 */
	public ArrayList<UInt16> getArrayListPoints1DUInt16() {
		synchronized (lock) {
		return arrayListPoints1DUInt16;
		}
	}

	/**
	 * @return the arrayListPoints3DInt
	 */
	public ArrayList<int[]> getArrayListPoints3DInt() {
		return arrayListPoints3DInt;
	}

	public void setBytes(byte[] data, int offsetParam) {
		int offset = offsetParam;
		type = data[offset++];// float32,uint16
		dimension = data[offset++]; // 2d,3d
		nbrPoints = ByteBuffer.wrap(
				new byte[] { data[offset], data[offset + 1], data[offset + 2],
						data[offset + 3] }).getInt(0);
		offset += 4;
		synchronized (lock) {

			if (type == INT32) {
				arrayListPoints3DInt = new ArrayList<int[]>();
			} else if (type == FLOAT32) {
				arrayListPoints3DFloat = new ArrayList<float[]>();
			} else if (type == FLOAT64) {
				arrayListPoints3DDouble = new ArrayList<double[]>();
			} else if( type == UINT16){
				Log.e("valueOPif","OK");
				arrayListPoints1DUInt16 = new ArrayList<UInt16>();
			}

			Log.e("valueOPif","nbr point : "+nbrPoints+"hum :" +(data.length - offsetParam)+" compare to : "+ (6 + nbrPoints
					* getSizeBytePerPoint()) );
			if (data.length - offsetParam >= 6 + nbrPoints
					* getSizeBytePerPoint()) {
				if (type == FLOAT64 || type == INT64 || type == UINT64) {
					for (int i = 0; i < nbrPoints; i++) {
						byte[] point3d_x = new byte[] { 
								data[offset + 7],
								data[offset + 6], 
								data[offset + 5],
								data[offset + 4], 
								data[offset + 3],
								data[offset + 2], 
								data[offset + 1],
								data[offset] };
						
						byte[] point3d_y = new byte[] { 
								data[offset+15],
								data[offset+14],
								data[offset+13],
								data[offset+12], 
								data[offset+11],
								data[offset+10], 
								data[offset+9],
								data[offset+8] };

						byte[] point3d_z =  new byte[] {
								data[offset+23],
								data[offset+22],
								data[offset+21],
								data[offset+20],
								data[offset+19],
								data[offset+18], 
								data[offset+17], 
								data[offset+16] };

						offset = offset + getSizeBytePerPoint();
						if (type == FLOAT64) {
							double[] point3d = new double[3];
							point3d[0] = ByteBuffer.wrap(point3d_x).getDouble(0);
							point3d[1] = ByteBuffer.wrap(point3d_y).getDouble(0);
							point3d[2] = ByteBuffer.wrap(point3d_z).getDouble(0);
							arrayListPoints3DDouble.add(point3d);
						}
					}
				} else if (type == UINT16){
					Log.e("valueOPif","allo");
					for (int i = 0; i < nbrPoints; i++) {
						byte[] point1d_x = new byte[] {  
								data[offset],
								data[offset +1] };

						offset = offset + getSizeBytePerPoint();
						//if (type == UINT16) {
							UInt16 point3d = UInt16.valueOfBigEndian(point1d_x);
							//Log.e("valueOPif","un uint16 : "+ point3d.intValue());
							arrayListPoints1DUInt16.add(point3d);
						//}
					}
					
				} else{
					for (int i = 0; i < nbrPoints; i++) {
						byte[] point3d_x = new byte[] { 
								data[offset + 3],
								data[offset + 2], 
								data[offset + 1],
								data[offset] };

						byte[] point3d_y = new byte[] { 
								data[offset + 7],
								data[offset + 6], 
								data[offset + 5],
								data[offset + 4] };

						byte[] point3d_z = new byte[] { 
								data[offset + 11],
								data[offset + 10], 
								data[offset + 9],
								data[offset + 8] };

						offset = offset + getSizeBytePerPoint();
						if (type == INT32) {
							int[] point3d = new int[3];
							point3d[0] = ByteBuffer.wrap(point3d_x).getInt(0);
							point3d[1] = ByteBuffer.wrap(point3d_y).getInt(0);
							point3d[2] = ByteBuffer.wrap(point3d_z).getInt(0);
							arrayListPoints3DInt.add(point3d);
						} else if (type == FLOAT32) {
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
	}
}
