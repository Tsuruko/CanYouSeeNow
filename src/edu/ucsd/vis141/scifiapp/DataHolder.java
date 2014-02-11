package edu.ucsd.vis141.scifiapp;

public class DataHolder {
	
	private String display = "";
	
	public String getDisplay() {
		return display;
	}
	
	public void setDisplay(String s) {
		display = s;
	}
	
	private static final DataHolder dataholder = new DataHolder();
	public static DataHolder getInstance() {
		return dataholder;
	}
	
}
