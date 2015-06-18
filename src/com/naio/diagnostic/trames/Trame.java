package com.naio.diagnostic.trames;

public class Trame {
	private byte[] naio01 = new byte[6];
	private byte id ;
	private byte[] size = new byte[4];
	private byte[] payload;
	private byte[] checksum = new byte[4];
	protected final int INT8 = 0;
	protected final int UINT8 = 1;
	protected final int INT16 = 2;
	protected final byte UINT16 = 3;
	protected final int INT32 = 4;
	protected final int UINT32 = 5;
	protected final int INT64 = 6;
	protected final int UINT64 = 7;
	protected final int FLOAT32 = 8;
	protected final int FLOAT64 = 9;
	protected byte type ;
	protected byte dimension;
	
	public Trame(byte[] naio01, byte id, byte[] size, byte[] payload, byte[] checksum) {
		super();
		this.naio01 = naio01;
		this.id = id;
		this.setSize(size);
		this.payload = payload;
		this.checksum = checksum;
	}
	
	public Trame(byte[] data){
		
	}
	
	public Trame() {
		
	}
	
	public int getSizeBytePerPoint() {
		int nbr1 = 0, nbr2 = 0;
		switch (type) {
		case INT8:
		case UINT8:
			nbr1 = 1;
			break;
		case INT16:
		case UINT16:
			nbr1 = 2;
			break;
		case INT32:
		case UINT32:
			nbr1 = 4;
			break;
		case INT64:
		case UINT64:
			nbr1 = 8;
			break;
		case FLOAT32:
			nbr1 = 4;
			break;
		case FLOAT64:
			nbr1 = 8;
			break;
		default:
			break;
		}
		switch (dimension) {
		case 1:
			nbr2 = 1;
			break;
		case 2:
			nbr2 = 2;
			break;
		case 3:
			nbr2 = 3;
			break;
		default:
			break;
		}
		return nbr1 * nbr2;

	}

	/**
	 * @return the naio01
	 */
	public byte[] getNaio01() {
		return naio01;
	}
	/**
	 * @return the id
	 */
	public byte getId() {
		return id;
	}
	/**
	 * @return the size
	 */
	public byte[] getSize() {
		return size;
	}
	/**
	 * @return the payload
	 */
	public byte[] getPayload() {
		return payload;
	}

	public byte[] getChecksum() {
		return checksum;
	}

	public String show() {
		return null;
	}

	public void setSize(byte[] size) {
		this.size = size;
	}

	
	
}
