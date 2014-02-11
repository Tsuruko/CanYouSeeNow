/*********************************
 * UCSD VIS 141A project
 * SciFiAPP
 * 
 * Created By: Monica Liu
 * Last Modified 2/10/14
 ********************************/

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
