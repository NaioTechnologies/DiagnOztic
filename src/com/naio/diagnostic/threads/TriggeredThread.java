package com.naio.diagnostic.threads;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import android.util.Log;

import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.NewMemoryBuffer;
import com.naio.net.NetClient;

public class TriggeredThread extends Thread {

	private final Object lock = new Object();
	private NewMemoryBuffer memoryBuffer;
	private NetClient netClient;
	private ByteBuffer buffer;
	private boolean read;
	private byte[] bytes;

	public TriggeredThread(NewMemoryBuffer memBuff, NetClient netClient) {
		this.memoryBuffer = memBuff;
		this.netClient = netClient;
		read = true;
		buffer = ByteBuffer.allocate(Config.BUFFER_SIZE);
	}

	public void triggerRead() {
		synchronized (lock) {
			read = true;
			lock.notify();
		}
	}

	public void run() {
		while (true) {
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				SocketChannel client = netClient.socketChannel;
				if (client == null)
					continue;
				if (read) {
					int charsRead = 0;
					try {
						if ((charsRead = client.read(buffer)) > -1) {

							if (charsRead > 0) {
								memoryBuffer.addToFifo(buffer.array(),
										charsRead, buffer.arrayOffset());
							}
							buffer.clear();

						} else {
							Log.e("states", "close");
							client.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					try {
						ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
						int idx = 0;
						byte[] buffarray = buffer.array();
						for (byte bit : bytes) {
							buffarray[idx++] = bit;
						}
						client.write(buffer);
						buffer.clear();
						read = true;
					} catch (IOException e) {
						e.printStackTrace();
					} 
				}
			}
		}

	}

	public NetClient getNetClient() {
		return netClient;
	}

	public void triggerWrite(byte[] bytesArray) {
		synchronized (lock) {
			read = false;
			this.bytes = bytesArray;
			lock.notify();
		}
	}

}
