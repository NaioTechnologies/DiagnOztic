package com.naio.diagnostic.threads;

import java.io.IOException;
import android.content.Context;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.MemoryBuffer;
import com.naio.diagnostic.utils.NewMemoryBuffer;
import com.naio.net.NetClient;

public class ReadSocketThread extends Thread {

	private NetClient netClient;
	private boolean stop;
	private final Object lock1 = new Object();
	private final Object lock2 = new Object();
	public ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
	private int port;
	private NewMemoryBuffer newmemoryBuffer;

	public ReadSocketThread(MemoryBuffer memoryBuffer, int port) {
		this.port = port;
		queue = new ConcurrentLinkedQueue<String>();
		this.stop = true;
	}

	public ReadSocketThread(NewMemoryBuffer memoryBuffer, int port) {
		this.port = port;
		this.newmemoryBuffer = memoryBuffer;
		queue = new ConcurrentLinkedQueue<String>();
		this.stop = true;
	}

	public void run() {
		int charsRead = 0;
		ByteBuffer buffer = ByteBuffer.allocate(Config.BUFFER_SIZE);
		if (port == Config.PORT_LOG)
			netClient = new NetClient(Config.HOST2, port, "0");
		else
			netClient = new NetClient(Config.HOST, port, "0");
		netClient.connectWithServer();
		try {
			while (this.stop) {
				if (netClient.socketChannel != null) {
					if ((charsRead = netClient.socketChannel.read(buffer)) > -1) {

						if(charsRead>0){
							newmemoryBuffer.addToFifo(buffer.array(), charsRead, buffer.arrayOffset());
							buffer.clear();
						}
						if(!netClient.testConnection()){
							netClient.disConnectWithServer();
							stop = false;
							}
						try {
							Thread.sleep(0, 10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
							Log.e("states","close");
							
					}
				} else {
					//Log.e("socket", "pas de in");
					/*try {
						Thread.sleep(1, 10);
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}*/
				}
			}
		} catch (IOException e) {
		
			e.printStackTrace();
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
