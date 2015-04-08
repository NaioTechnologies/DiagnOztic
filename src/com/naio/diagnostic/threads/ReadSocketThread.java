package com.naio.diagnostic.threads;

import java.io.IOException;
import android.content.Context;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
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

	private NetClient netClient;
	private boolean stop;
	private final Object lock1 = new Object();
	private final Object lock2 = new Object();
	private int port;
	private NewMemoryBuffer newmemoryBuffer;
	private ByteBuffer buffer;

	public ReadSocketThread(MemoryBuffer memoryBuffer, int port) {
		this.port = port;
		this.stop = true;
	}

	public ReadSocketThread(NewMemoryBuffer memoryBuffer, int port) {
		this.port = port;
		this.newmemoryBuffer = memoryBuffer;
		this.stop = true;
		buffer = ByteBuffer.allocate(Config.BUFFER_SIZE);
	}

	public void run() {
		int charsRead = 0;

		if (port == Config.PORT_LOG)
			netClient = new NetClient(Config.HOST2, port, "0");
		else
			netClient = new NetClient(Config.HOST, port, "0");
		netClient.connectWithServer();
		Selector selector = null;

		try {
			selector = Selector.open();
			SelectionKey key = netClient.socketChannel.register(selector,
					SelectionKey.OP_READ);
		} catch (ClosedChannelException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

					if (key.isAcceptable()) {
						// a connection was accepted by a ServerSocketChannel.

					} else if (key.isConnectable()) {
						// a connection was established with a remote server.

					} else if (key.isReadable()) {
						if (netClient.socketChannel != null) {
							if ((charsRead = netClient.socketChannel
									.read(buffer)) > -1) {

								if (charsRead > 0) {
									Log.e("charsRead", "" + charsRead);
									newmemoryBuffer.addToFifo(buffer.array(),
											charsRead, buffer.arrayOffset());
								}
								buffer.clear();
								if (!netClient.testConnection()) {
									netClient.disConnectWithServer();
									stop = false;
								}

							} else {
								Log.e("states", "close");
								if (!netClient.testConnection()) {
									netClient.disConnectWithServer();
									stop = false;
								}

							}
						} else {
							if (!netClient.testConnection()) {
								netClient.disConnectWithServer();
								stop = false;
							}
							// Log.e("socket", "pas de in");

						}

					} else if (key.isWritable()) {
						// a channel is ready for writing
					}

					keyIterator.remove();
				}

			}
		} catch (IOException e) {

			e.printStackTrace();
		}
		netClient.disConnectWithServer();

	}

	/**
	 * @param stop
	 *            the stop to set
	 */
	public void setStop(boolean stop) {
		synchronized (lock2) {
			this.stop = stop;
			netClient.disConnectWithServer();
		}

	}
}
