package com.naio.diagnostic.trames;

import java.util.Arrays;

import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.DataManager;

public class JPEGTrame extends Trame{

	public JPEGTrame(byte[] data) {
		super(data);
		
			if (data.length > 0) {
				DataManager.getInstance().fifoImage.offer(data);
			}
	}
}
