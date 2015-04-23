package com.naio.diagnostic.trames;

public class StringTrame extends Trame {

	private String text;
	private byte id;

	public StringTrame(byte[] data) {
		super(data);
		text = "";
		boolean first= true;
		
		for(byte bit : data){
			if(first){
				id = bit;
				first = false;
			}
			else
				text += (char)bit;
		}
	}

	/**
	 * @return the id
	 */
	public byte getId() {
		return id;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	
	

}
