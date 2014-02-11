/*********************************
 * UCSD VIS 141A project
 * SciFiAPP
 * 
 * Created By: Monica Liu
 * Last Modified 2/10/14
 * 
 * DataHolder.java:
 *   Global Singleton Class for holding information regarding
 *   what elements the DrawView should draw.  The information is set
 *   from the camera's preview callback and used to draw the overlay.
 *   
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
