package com.naio.diagnostic.views;

import android.util.Log;

import com.naio.diagnostic.threads.SelectorThread;
import com.naio.diagnostic.threads.SendSocketThread;
import com.naio.diagnostic.views.AnalogueView.OnMoveListener;

public class MyMoveListenerForAnalogueView implements OnMoveListener {
	private SelectorThread sendSocketThreadMotors;
	private byte left;
	private byte right;
	private int index;
	private static final Object lock = new Object();

	public MyMoveListenerForAnalogueView(SelectorThread sendSocketThreadMotors, int idx) {
		this.sendSocketThreadMotors = sendSocketThreadMotors;
		this.index = idx;
	}

	public void sendMotorsCommand() {
		synchronized (lock) {

			byte[] b = new byte[] { 
					78, 65, 73, 79, 48, 49,
					1, 
					0, 0, 0, 2,
					left, right,
					0, 0, 0, 0 };
			sendSocketThreadMotors.setBytesToWriteForThread(b,index);
		}
	}

	@Override
	public void onMaxMoveInDirection(int padDiff, int padSpeed) {
		Log.e("ghjk",padDiff +"-----"+ padSpeed);
		/*int bearing = padDiff * 127 / 180;
		byte xa = 0;
		byte ya = 0;
		if (padSpeed >= 0) {
			if (padSpeed + bearing > 127)
				xa = (byte) 127;
			else {
				if (padSpeed + bearing < -127)
					xa = (byte) -127;
				else
					xa = (byte) (padSpeed + bearing);
			}

			if (padSpeed - bearing < -127)
				ya = (byte) -127;
			else {
				if (padSpeed - bearing > 127)
					ya = (byte) 127;
				else
					ya = (byte) (padSpeed - bearing);
			}

		} else {
			if (padSpeed - bearing < -127)
				xa = (byte) -127;
			else {
				if (padSpeed - bearing > 127)
					xa = (byte) 127;
				else
					xa = (byte) (padSpeed - bearing);
			}
			if (padSpeed + bearing > 127)
				ya = (byte) 127;
			else {
				if (padSpeed + bearing < -127)
					ya = (byte) -127;
				else
					ya = (byte) (padSpeed + bearing);
			}
		}
		synchronized (lock) {

			left = xa;
			right = ya;
		}
*/	synchronized (lock) {

	left =(byte) padDiff;
	right = (byte)padSpeed;
		}

	}

	@Override
	public void onHalfMoveInDirection(int padDiff, int padSpeed) {
		/*int bearing = padDiff * 127 / 180;
		byte xa = 0;
		byte ya = 0;
		if (padSpeed >= 0) {
			if (padSpeed + bearing > 127)
				xa = (byte) 127;
			else {
				if (padSpeed + bearing < -127)
					xa = (byte) -127;
				else
					xa = (byte) (padSpeed + bearing);
			}

			if (padSpeed - bearing < -127)
				ya = (byte) -127;
			else {
				if (padSpeed - bearing > 127)
					ya = (byte) 127;
				else
					ya = (byte) (padSpeed - bearing);
			}

		} else {
			if (padSpeed - bearing < -127)
				xa = (byte) -127;
			else {
				if (padSpeed - bearing > 127)
					xa = (byte) 127;
				else
					xa = (byte) (padSpeed - bearing);
			}
			if (padSpeed + bearing > 127)
				ya = (byte) 127;
			else {
				if (padSpeed + bearing < -127)
					ya = (byte) -127;
				else
					ya = (byte) (padSpeed + bearing);
			}
		}
		synchronized (lock) {
			left = xa;
			right = ya;
		}*/
		
		synchronized (lock) {

			left =(byte) padDiff;
			right = (byte)padSpeed;
				}

	}

}
