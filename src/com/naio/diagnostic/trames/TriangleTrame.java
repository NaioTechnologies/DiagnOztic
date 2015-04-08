package com.naio.diagnostic.trames;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.naio.diagnostic.utils.Config;

public class TriangleTrame extends Trame{

	private byte type;
	private byte dimension;
	private int nbrTriangles;
	private ArrayList<int[]> arrayListTriangles3DInt;
	private ArrayList<float[]> arrayListTriangles3DFloat;

	public TriangleTrame(byte[] data) {
		super(data);
		int offset = 0;
		type = data[offset++];// int float etc
		dimension = data[offset++];//2d 3d
		nbrTriangles = ByteBuffer.wrap(new byte[] {
						data[offset],
						data[offset + 1], 
						data[offset + 2],
						data[offset + 3] })
						.getInt(0);
		offset += 4;

		if (type == 0x0) {
			arrayListTriangles3DInt = new ArrayList<int[]>();
		} else if (type == 0x9) {
			arrayListTriangles3DFloat = new ArrayList<float[]>();
		}

		if (data.length == 6 + nbrTriangles * 4 * 3 * 3) {
			for (int i = 0; i < nbrTriangles; i++) {
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
				
				byte[] point3d_x_2 = new byte[] { 
						data[offset + 15],
						data[offset + 14],
						data[offset + 13],
						data[offset+12] };
				
				byte[] point3d_y_2 = new byte[] {
						data[offset + 19],
						data[offset + 18],
						data[offset + 17],
						data[offset + 16] };
				
				byte[] point3d_z_2 = new byte[] { 
						data[offset + 23],
						data[offset + 22], 
						data[offset + 21],
						data[offset + 20] };
				
				byte[] point3d_x_3 = new byte[] { 
						data[offset + 27],
						data[offset + 26], 
						data[offset + 25], 
						data[offset+24] };
				
				byte[] point3d_y_3 = new byte[] {
						data[offset + 31],
						data[offset + 30],
						data[offset + 29],
						data[offset + 28] };
				
				byte[] point3d_z_3 = new byte[] {
						data[offset + 35],
						data[offset + 34], 
						data[offset + 33],
						data[offset + 32] };
				
				offset = offset + Config.POINTS3D_SIZE_IN_BYTES*3;
				if (type == 0) {
					int[] point3d = new int[9];
					point3d[0] = ByteBuffer.wrap(point3d_x).getInt(0);
					point3d[1] = ByteBuffer.wrap(point3d_y).getInt(0);
					point3d[2] = ByteBuffer.wrap(point3d_z).getInt(0);
					point3d[3] = ByteBuffer.wrap(point3d_x_2).getInt(0);
					point3d[4] = ByteBuffer.wrap(point3d_y_2).getInt(0);
					point3d[5] = ByteBuffer.wrap(point3d_z_2).getInt(0);
					point3d[6] = ByteBuffer.wrap(point3d_x_3).getInt(0);
					point3d[7] = ByteBuffer.wrap(point3d_y_3).getInt(0);
					point3d[8] = ByteBuffer.wrap(point3d_z_3).getInt(0);
					arrayListTriangles3DInt.add(point3d);
				} else if (type == 0x9) {
					float[] point3d = new float[9];
					point3d[0] = ByteBuffer.wrap(point3d_x).getFloat(0);
					point3d[1] = ByteBuffer.wrap(point3d_y).getFloat(0);
					point3d[2] = ByteBuffer.wrap(point3d_z).getFloat(0);
					point3d[3] = ByteBuffer.wrap(point3d_x_2).getFloat(0);
					point3d[4] = ByteBuffer.wrap(point3d_y_2).getFloat(0);
					point3d[5] = ByteBuffer.wrap(point3d_z_2).getFloat(0);
					point3d[6] = ByteBuffer.wrap(point3d_x_3).getFloat(0);
					point3d[7] = ByteBuffer.wrap(point3d_y_3).getFloat(0);
					point3d[8] = ByteBuffer.wrap(point3d_z_3).getFloat(0);
					arrayListTriangles3DFloat.add(point3d);
				}
			}
		}
	}

	/**
	 * @return the arrayListTriangles3DInt
	 */
	public ArrayList<int[]> getArrayListTriangles3DInt() {
		return arrayListTriangles3DInt;
	}

	/**
	 * @return the arrayListTriangles3DFloat
	 */
	public ArrayList<float[]> getArrayListTriangles3DFloat() {
		return arrayListTriangles3DFloat;
	}
	
	

}
