package com.naio.diagnostic.threads;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.MemoryBuffer;
import com.naio.net.NetClient;

public class SendSocketThread extends Thread {

	private boolean stop;
	private final Object lock1 = new Object();
	private final Object lock2 = new Object();
	private byte[] bytes;
	private NetClient netClient;
	private int port;

	public SendSocketThread(byte[] bytes) {
		this.bytes = bytes;
		this.stop = true;
	}

	public SendSocketThread(int port) {
		this.stop = true;
		this.port = port;
	}

	public void setBytes(byte[] bytes) {
		synchronized (lock1) {
			this.bytes = bytes;
			lock1.notify();
		}
	}

	public void run() {
		netClient = new NetClient(Config.HOST, port, "0");
		netClient.connectWithServer();
		
		while (stop) {
			synchronized (lock1) {
				try {
					lock1.wait();
					ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
					int idx = 0;
					byte[] buffarray = buffer.array();
					for (byte bit : bytes) {
						buffarray[idx++] = bit;
					}
					netClient.socketChannel.write(buffer);
					buffer.clear();
					Thread.sleep(10, 0);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	/**
	 * @param stop
	 *            the stop to set
	 */
	public void setStop(boolean stop) {
		synchronized (lock2) {
			this.stop = stop;
		}

	}

}
