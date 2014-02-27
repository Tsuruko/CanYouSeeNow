package edu.ucsd.vis141.CanYouSeeNow;

import java.util.Timer;
import java.util.TimerTask;

import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

/*********************************
 * UCSD VIS 141A project
 * SciFiAPP
 * 
 * Created By: Monica Liu
 * Last Modified 2/10/14
 * 
 * CameraActivity.java
 *   Main Activity holding the camera display. Constantly updates the
 *   surface overlay drawView in a timer task loop
 *   The camera is locked to landscape to prevent some stretching
 *   and rotation of the camera preview.
 *   
 ********************************/

public class CameraActivity extends Activity {
	
	private Camera mCamera;
	private CameraPreview mPreview;
	private DrawView detectEdge;
	Timer timer = new Timer();
	private ImageView overlay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			View decorView = getWindow().getDecorView();
			//Hide the status bar
			int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
			decorView.setSystemUiVisibility(uiOptions);
			//Hide the action bar
			ActionBar actionBar = getActionBar();
			actionBar.hide();
		}
			
		mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        detectEdge = new DrawView(this);
        //overlay = (ImageView) findViewById(R.id.imageView1);
        
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        preview.addView(detectEdge);
        timer.schedule(new reDraw(), 0, 100); 
        
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		timer.cancel();
		mCamera.stopPreview();
		mCamera.release();
	}
	
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
	
	class reDraw extends TimerTask {
		@Override
		public void run() {
			CameraActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					//continuously refresh the drawview surface
					detectEdge.invalidate();
					//overlay.setImageBitmap(DataHolder.getInstance().getBitmap());
					//overlay.invalidate();
				}
			});	
		}
	};
	
	 public void modeSwitch1(View view) {
		 DataHolder.getInstance().setMode(R.integer.regular);
	 }

	 public void modeSwitch2(View view) {
		 DataHolder.getInstance().setMode(R.integer.blur);
	 }
	 
	 public void modeSwitch3(View view) {
		 DataHolder.getInstance().setMode(R.integer.other);
	 }
	 
	 public void modeSwitch4(View view) {
		 DataHolder.getInstance().setMode(R.integer.otherr); 
	 }
	 
}
