/*********************************
 * UCSD VIS 141A project
 * SciFiAPP
 * 
 * Created By: Monica Liu
 * Last Modified 2/10/14
 * 
 * DrawView.java:
 *   A drawview class used to overlay the sufaceview holding the camera preview.
 *   The edges found in the image are drawn on this view using canvas and paint.
 *   
 ********************************/

package edu.ucsd.vis141.scifiapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.view.SurfaceView;

public class DrawView extends SurfaceView{
	
	private Paint textPaint = new Paint();

	public DrawView(Context context) {
		super(context);
		
		textPaint.setARGB(255, 200, 0, 0);
		textPaint.setTextSize(60);
		
		setWillNotDraw(false);
	}

	@Override
	protected void onDraw(Canvas canvas){
		canvas.drawColor(0, Mode.CLEAR);
	    canvas.drawText(DataHolder.getInstance().getDisplay(), 500, 500, textPaint);
	}
	
}
