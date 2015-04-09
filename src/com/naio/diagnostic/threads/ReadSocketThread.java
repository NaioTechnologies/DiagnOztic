package com.naio.diagnostic.threads;

import java.io.IOException;
import android.content.Context;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.DataManager;
import com.naio.diagnostic.utils.MemoryBuffer;
import com.naio.diagnostic.utils.NewMemoryBuffer;
import com.naio.net.NetClient;

public class ReadSocketThread extends Thread {

	private boolean stop;
	private final Object lock1 = new Object();
	private final Object lock2 = new Object();
	private ByteBuffer buffer;
	private Selector selector;
	private TriggeredThread[] threadArray = new TriggeredThread[3];
	private int idx = 0;

	public ReadSocketThread(MemoryBuffer memoryBuffer, int port) {
		this.stop = true;
	}

	public ReadSocketThread(NewMemoryBuffer memoryBuffer, int port) {
		this.stop = true;
		idx = 0;
		buffer = ByteBuffer.allocate(Config.BUFFER_SIZE);
		
		addSocket(memoryBuffer,port);
	}

	public void addSocket(NewMemoryBuffer mem, int port) {
		NetClient netClient = new NetClient(Config.HOST, port, "0");
		threadArray[idx] = new TriggeredThread(mem, netClient);
		idx++;
	}

	public void run() {
		int charsRead = 0;
		selector = null;
		try {
			selector = Selector.open();

		} catch (IOException e2) {
			e2.printStackTrace();
		}
		for(int i = 0; i < threadArray.length;i++){
			if(threadArray[i] == null)
				continue;
			NetClient netClient = threadArray[i].getNetClient();
			netClient.connectWithServer();
			if (netClient.socketChannel.isConnected() == false) {
				continue;
			}
			int ops = netClient.socketChannel.validOps();
			SelectionKey keya;
			try {
				keya = netClient.socketChannel.register(selector, ops);
				keya.attach(i);
			} catch (ClosedChannelException e1) {
				e1.printStackTrace();
			}
			threadArray[i].start();
		}
		try {
			while (this.stop) {
				int readyChannels = selector.select();

				if (readyChannels == 0)
					continue;

				Set<SelectionKey> selectedKeys = selector.selectedKeys();

				Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
				while (keyIterator.hasNext()) {

					SelectionKey key = keyIterator.next();
					int index = (int) key.attachment();
					if (key.isAcceptable()) {

						key.channel().register(selector, SelectionKey.OP_READ);

					} else if (key.isConnectable()) {
						// a connection was established with a remote server.

					} else if (key.isReadable()) {
						
						threadArray[index].triggerRead();
						
					} else if (key.isWritable()) {
						// a channel is ready for writing
						threadArray[index].triggerWrite();
					}

					keyIterator.remove();
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
			for(int i = 0; i < threadArray.length;i++){
				if(threadArray[i] == null)
					continue;
				threadArray[i].interrupt();
			}
		}

	}
}
