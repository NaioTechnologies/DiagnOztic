package com.naio.diagnostic.packet;

import com.naio.diagnostic.trames.Trame;

public class BasePacket extends Trame{

	protected static final byte STRING = 0x00;
	protected static final byte POINTS = 0x01;
	protected static final byte LINES = 0x02;
	protected static final byte TRIANGLES = 0x03;
	protected static final byte JPEG = 0x04;
	
	public BasePacket(byte[] data) {
		super(data);
		
	}

}
