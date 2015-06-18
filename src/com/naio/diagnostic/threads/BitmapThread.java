package com.naio.diagnostic.threads;

import java.util.ArrayList;

import com.naio.diagnostic.packet.OdometryPacket;
import com.naio.diagnostic.trames.StringTrame;
import com.naio.diagnostic.trames.TrameDecoder;
import com.naio.diagnostic.utils.DataManager;
import com.naio.diagnostic.utils.GrayToChromadepth;
import com.naio.diagnostic.utils.NewMemoryBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class BitmapThread extends Thread {

	private BitmapFactory.Options opt;
	private NewMemoryBuffer memoryBufferLog;
	private byte[] oldPollFifo;
	private TrameDecoder trameDecoder;
	private boolean continueTheThread;
	public static Object lock = new Object();
	public static Object lock2 = new Object();
	public static int threadIndex = 0;
	private int nbrOfTimeTheSameShit = 0;
	private GrayToChromadepth grayToChromadepth;

	public BitmapThread(NewMemoryBuffer memBuff) {
		opt = new BitmapFactory.Options();
		opt.inMutable = true;
		opt.inPurgeable = true;
		threadIndex++;
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		continueTheThread = true;
		trameDecoder = new TrameDecoder();
		memoryBufferLog = memBuff;
		grayToChromadepth = new GrayToChromadepth();
		grayToChromadepth.compute_lut();
	}

	public void quit() {
		continueTheThread = false;
	}

	public void run() {
		while (continueTheThread) {

			Log.e("thread", "beginning + " + threadIndex);

			Log.e("thread", "test old fifo");
			byte[] pollFifo = null;

			synchronized (memoryBufferLog.lock) {
				try {
					memoryBufferLog.lock.wait(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			pollFifo = memoryBufferLog.getPollFifo();
			Log.e("notify", "fifo took in bitmap thread");
			if (oldPollFifo == pollFifo) {
				nbrOfTimeTheSameShit++;
				if (nbrOfTimeTheSameShit > 5) {
					try {
						Thread.sleep(1, 100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				continue;
			}
			oldPollFifo = pollFifo;
			trameDecoder.decode(pollFifo);
			OdometryPacket odoPacket = trameDecoder.getOdometryPacket();
			Log.e("notify", "decode the fifo in bitmap thread");
			if (odoPacket == null)
				continue;
			if (odoPacket.getJpegtrame() == null)
				continue;
			if (odoPacket.getJpegtrame().getImgData() == null)
				continue;
			Bitmap mutableBitmap;
			System.gc();
			/*
			 * synchronized (lock) { try { lock.wait(100); } catch
			 * (InterruptedException e) { e.printStackTrace(); } }
			 */
			byte[] dataf = odoPacket.getJpegtrame().getImgData();
			mutableBitmap = BitmapFactory.decodeByteArray(dataf, 0,dataf.length, opt);

			Log.e("thread", "decode done");
			dataf = null;
			if (mutableBitmap == null)
				continue;
			if (odoPacket.getPointtrame() == null) {
				DataManager.getInstance().offerfifoBitmap(mutableBitmap);
				continue;
			}
			ArrayList<float[]> dataPoints3d = odoPacket.getPointtrame().getArrayListPoints3DFloat();
			if (dataPoints3d == null) {
				DataManager.getInstance().offerfifoBitmap(mutableBitmap);
				continue;
			}

			Canvas canvas = new Canvas(mutableBitmap);
			Paint paint = new Paint();
			for (int i = 0; i < dataPoints3d.size(); i++) {
				float x = dataPoints3d.get(i)[0];
				float y = dataPoints3d.get(i)[1];
				float z = dataPoints3d.get(i)[2];
				
				paint.setAntiAlias(true);
				int[] rgb = new int[3];
				if (z > 255 || z < 0)
					paint.setColor(Color.rgb(255, 255, 255));
				else {
					grayToChromadepth.getRGBFromZ(z, rgb);
					paint.setColor(Color.rgb(rgb[0], rgb[1], rgb[2]));
					canvas.drawCircle(x - 1, y - 1, 4, paint);
				}

				/*
				 * if (arrayPoints3d.size() <= i - 1) { arrayPoints3d.add(new
				 * float[] { x, y }); } else { arrayPoints3d.get(i - 1)[0] = x;
				 * arrayPoints3d.get(i - 1)[1] = y; } if (i - 1 <=
				 * arrayPoints3d.size() && i == (dataPoints3d.size() - 1)) { int
				 * s = arrayPoints3d.size(); for (int j = (i - 1); j < s; j++) {
				 * arrayPoints3d.remove(j); } }
				 */
			}

			if (odoPacket.getLinetrame() == null) {
				DataManager.getInstance().offerfifoBitmap(mutableBitmap);
				continue;
			}
			ArrayList<float[]> dataLines3d = odoPacket.getLinetrame()
					.getArrayListLines3DFloat();
			if (dataLines3d == null) {
				DataManager.getInstance().offerfifoBitmap(mutableBitmap);
				continue;
			}

			for (int i = 0; i < dataLines3d.size(); i++) {
				float x = dataLines3d.get(i)[0];
				float y = dataLines3d.get(i)[1];
				float z = dataLines3d.get(i)[2];
				float x2 = dataLines3d.get(i)[3];
				float y2 = dataLines3d.get(i)[4];
				float z2 = dataLines3d.get(i)[5];

				int[] rgb = new int[3];
				if (z > 255 || z < 1 || x < 0 || y <0 || x2 <0 || z2 <0 || y2 <0)
					paint.setColor(Color.rgb(255, 255, 255));
				else {
					grayToChromadepth.getRGBFromZ(z, rgb);
					paint.setColor(Color.rgb(rgb[0], rgb[1], rgb[2]));
					if(rgb[2] > 250)
						Log.e("sdfghjk",""+rgb[0] +"___" +rgb[1]+"_____"+ rgb[2]+ "____" + x+"___"+y+"____"+z+"___"+x2+"___"+y2+"____"+i);
					canvas.drawLine(x, y, x2, y2, paint);
				}
				
				
				/*
				 * if (arrayPoints3d.size() <= i - 1) { arrayPoints3d.add(new
				 * float[] { x, y }); } else { arrayPoints3d.get(i - 1)[0] = x;
				 * arrayPoints3d.get(i - 1)[1] = y; } if (i - 1 <=
				 * arrayPoints3d.size() && i == (dataPoints3d.size() - 1)) { int
				 * s = arrayPoints3d.size(); for (int j = (i - 1); j < s; j++) {
				 * arrayPoints3d.remove(j); } }
				 */
			}
			DataManager.getInstance().offerfifoBitmap(mutableBitmap);
			Log.e("thread", "offer so the end");

			if (odoPacket.getStringtrames() == null)
				continue;

			StringTrame[] testa = odoPacket.getStringtrames();
			String[] test = new String[4];
			int idx = 0;
			for(StringTrame t : testa){
				if( t != null){
					test[idx++] = t.getText();
				}
			}
			DataManager.getInstance().offerStringOdoPacket(test);

		}
	}
}
