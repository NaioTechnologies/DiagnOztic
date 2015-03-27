package com.naio.diagnostic.trames;

public class StringTrame extends Trame {

	private String text;

	public StringTrame(byte[] data) {
		super(data);
		text = "";
		for(byte bit : data){
			text += (char)bit;
		}
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	
	

}
