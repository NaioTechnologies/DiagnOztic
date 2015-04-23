package com.naio.diagnostic.packet;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.naio.diagnostic.trames.StringTrame;
import com.naio.diagnostic.utils.Config;

public class StringPacket extends BasePacket{

	private StringTrame[] stringtrames = new StringTrame[12];
	

	public StringPacket(byte[] data) {
		int offset = Config.LENGHT_FULL_HEADER;
		while (offset < data.length - Config.LENGHT_FULL_HEADER) {
			switch (data[offset]) {
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
				StringTrame stringtrame = new StringTrame(Arrays.copyOfRange(data,offset,offset+sizeString));
				stringtrames[stringtrame.getId()] = stringtrame;
				offset+=sizeString+1;
				break;
			}
		}
	}


	/**
	 * @return the stringtrames
	 */
	public StringTrame[] getStringtrames() {
		return stringtrames;
	}
	
	
}
