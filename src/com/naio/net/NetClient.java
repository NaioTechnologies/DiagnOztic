package com.naio.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NetClient {

	/**
	 * Maximum size of buffer
	 */
	private Socket socket = null;
	private OutputStream out = null;
	private InputStream in = null;

	private String host = null;
	private String macAddress = null;
	private int port = 7999;
	public SocketChannel socketChannel;

	/**
	 * Constructor with Host, Port and MAC Address
	 * 
	 * @param host
	 * @param port
	 * @param macAddress
	 */
	public NetClient(String host, int port, String macAddress) {
		this.host = host;
		this.port = port;
		this.macAddress = macAddress;
	}

	public void connectWithServer() {
		try {
			if (socketChannel == null) {
				socketChannel = SocketChannel.open();
				socketChannel.configureBlocking(false);
				socketChannel.connect(new InetSocketAddress(this.host,this.port));
				while (!socketChannel.finishConnect()) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the out
	 */
	public OutputStream getOut() {
		return out;
	}

	/**
	 * @return the in
	 */
	public InputStream getIn() {
		return in;
	}

	public void disConnectWithServer() {
		if (socketChannel != null) {

			try {
				socketChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public boolean testConnection() {
		ByteBuffer buff = null;
		buff = ByteBuffer.allocate(1);
		buff.array()[0] = 0x1;
		int harRead = -1;
		try {
			harRead = socketChannel.write(buff);
		} catch (IOException e) {
			e.printStackTrace();
		}
		buff.clear();
		return harRead > -1;
	}

	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}