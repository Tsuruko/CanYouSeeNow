package edu.ucsd.vis141.CanYouSeeNow;

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
	
	private Bitmap drawing;
	private boolean status = false;
	private int mode = 0;
	
	public Bitmap getBitmap() {
		return drawing;
	}
	
	public void setBitmap(Bitmap m) {
		//drawing.recycle();
		drawing = m;
	}
	
	public boolean getStatus() {
		return status;
	}
	
	public void setStatus() {
		if (drawing != null) status = true;
	}
	
	public int getMode() {
		return mode;
	}
	
	public void setMode(int i) {
		if (i == R.integer.blur || i == R.integer.regular || i == R.integer.other || i == R.integer.otherr) {
			mode = i;
		}
	}
	
	//static declarations forcing one instance of this class
	private static final DataHolder dataholder = new DataHolder();
	public static DataHolder getInstance() {
		return dataholder;
	}
	
}
