package edu.ucsd.vis141.CanYouSeeNow;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.view.SurfaceView;

/*********************************
 * UCSD VIS 141A project
 * SciFiAPP
 * 
 * Created By: Monica Liu
 * Last Modified 3/03/14
 * 
 * DrawView.java:
 *   A drawview class used to overlay the sufaceview holding the camera preview.
 *   The edges found in the image are drawn on this view using canvas and paint.
 *   
 *   Note: possibly use a imageView instead of this class
 *   
 ********************************/

public class DrawView extends SurfaceView{

	public DrawView(Context context) {
		super(context);
		
		setWillNotDraw(false);
	}

	@Override
	protected void onDraw(Canvas canvas){
		if (DataHolder.getInstance().getImageMode()) canvas.drawBitmap(DataHolder.getInstance().getImgBitmap(), 0, 0, null);
		if (DataHolder.getInstance().getStatus()) canvas.drawBitmap(DataHolder.getInstance().getBitmap(), 0, 0, null);
	}
	
}
