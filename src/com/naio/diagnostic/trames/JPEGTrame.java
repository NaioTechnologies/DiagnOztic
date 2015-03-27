package com.naio.diagnostic.trames;

import java.util.Arrays;

import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.DataManager;

public class JPEGTrame extends Trame {
	byte[] imgData = null;
	public JPEGTrame(byte[] data) {
		super(data);

		if (data.length > 0) {
			imgData = data;
		}
	}
	/**
	 * @return the imgData
	 */
	public byte[] getImgData() {
		return imgData;
	}
	
}
