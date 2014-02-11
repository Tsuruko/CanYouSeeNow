/*********************************
 * UCSD VIS 141A project
 * SciFiAPP
 * 
 * Created By: Monica Liu
 * Last Modified 2/10/14
 * 
 * CameraPreview.java:
 *   A SurfaceView/SurfaceHolder callback for holding the camera preview
 *   Takes a pictures every 10 preview frame updates and uses it to find edges
 *   in the environment
 *   
 ********************************/

package edu.ucsd.vis141.scifiapp;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	
    private static final String TAG = null;
	private SurfaceHolder mHolder;
    private Camera mCamera;
    private int count = 0;

    //default constructor
    public CameraPreview(Context context) {
    	super(context);
    }
    
    //constructor to create a camera preview
    @SuppressWarnings("deprecation")
	public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        
        // deprecated setting, but required on Android versions prior to 3.0
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        	getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewCallback(previewCallback);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size size = getBestPreviewSize(w, h, parameters);
        if (size != null) {
            parameters.setPreviewSize(size.width, size.height);
            Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
        }
        mCamera.setParameters(parameters);

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
    
    Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
    	public void onPreviewFrame(byte[] data, Camera camera) {
            if (data == null) return;
            Camera.Size size = mCamera.getParameters().getPreviewSize();
            if (size == null) return;
            
    		if (count > 10) {
    			//take pic and analyze
    			DataHolder.getInstance().setDisplay("UPDATE");
    			findEdges();
    			count = 0;
    		} else {
    			DataHolder.getInstance().setDisplay("TESTDRAW");
    			count++;
    		}
    	}
    };
    
    //calculate where the edges are, return data to the global data holder
    protected void findEdges() {
    	
    }
    
    //adjust preview size to prevent distortion/stretching
    private static Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) result = size;
                }
            }
        }

        return result;
    }
}