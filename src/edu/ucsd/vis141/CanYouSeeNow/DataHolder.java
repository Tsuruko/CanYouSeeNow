package edu.ucsd.vis141.CanYouSeeNow;

import android.graphics.Bitmap;

/*********************************
 * UCSD VIS 141A project
 * SciFiAPP
 * 
 * Created By: Monica Liu
 * Last Modified 3/03/14
 * 
 * DataHolder.java:
 *   Global Singleton Class for holding information regarding
 *   what elements the DrawView should draw.  The information is set
 *   from the camera's preview callback and used to draw the overlay.
 *   
 *   Also holds the static constants used to determine display mode for the bitmap overlay
 *   
 ********************************/

public class DataHolder {
	
	//static constants for determining overlay mode
	public final static int LIGHT = 0;
	public final static int TRANS = 1;
	public final static int BLUR = 2;
	public final static int BLUR_TRANS = 3;
	public final static int DARK = 4;
	public final static int DARK_TRANS = 5;
	public final static int DARK_BLUR = 6;
	public final static int DARK_BLUR_TRANS = 7;
	
	//data values which need to have only one instance and be viewed from all classes across the application
	private Bitmap drawing;
	private boolean status = false;
	private int mode = 1;
	
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
		if (i <= DARK_BLUR_TRANS) mode = i;
	}
	
	//static declarations forcing one instance of this class
	private static final DataHolder dataholder = new DataHolder();
	public static DataHolder getInstance() {
		return dataholder;
	}
	
}
