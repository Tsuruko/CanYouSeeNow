package edu.ucsd.vis141.scifiapp;

import android.graphics.Bitmap;

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

public class DataHolder {
	
	private String display = "";
	private Bitmap drawing;
	private boolean status = false;
	
	public Bitmap getBitmap() {
		return drawing;
	}
	
	public void setBitmap(Bitmap m) {
		//drawing.recycle();
		drawing = m;
	}
	
	public String getDisplay() {
		return display;
	}
	
	public void setDisplay(String s) {
		display = s;
	}
	
	public boolean getStatus() {
		return status;
	}
	
	public void setStatus() {
		if (drawing != null) status = true;
	}
	
	//static declarations forcing one instance of this class
	private static final DataHolder dataholder = new DataHolder();
	public static DataHolder getInstance() {
		return dataholder;
	}
	
}
