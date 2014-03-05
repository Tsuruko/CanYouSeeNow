package edu.ucsd.vis141.CanYouSeeNow;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

public class Blur {
	
	private final static int RESIZE = 200;
	
	private Bitmap sourceImage;
	private Bitmap outputImage;
	
	private float sigma;
	
	private int blurRadius;
	
	private int height, width;
	private int originH, originW, ratio;
	
	float [][] kernel;
	
	// constructors	
    //Constructs a new detector with default parameters.
	public Blur() {
		blurRadius = 4;
		sigma = 1f;
	}
	
	//Constructs a new detector with an image
	public Blur(Bitmap source) {
		this.sourceImage = source;
		blurRadius = 4;
		sigma = 1f;
	}

	//change source bitmap
	public void setSourceImage(Bitmap m) {
		if (sourceImage == null) sourceImage = m;
		else {
			sourceImage.recycle();
			sourceImage = m;
		}
	}
	
	public void blur() {
		initialize();
		outputImage = resize(sourceImage);
		outputImage = blur(outputImage);
		outputImage = restore(outputImage);
		writeData();
	}
	
	private void makeKernel(int radius) {
		//create the gaussian filter kernel for removing noise
		kernel = new float [radius][radius];
		float sum = 0;
		for (int y = 0; y < radius; y++) {
			for (int x = 0; x < radius; x++) {
				int xx = x - radius/2;
				int yy = y - radius/2;
				kernel[y][x] = (float) Math.exp(-((xx*xx) + (yy * yy))/ (2 * (sigma * sigma)));
				sum += kernel[y][x];
			}
		}
		for (int i = 0; i < radius; i++){
			for (int j = 0; j < radius; j++) {
				kernel[i][j] /= sum;
			}
		}
	}
	
	private void initialize() {
		//set variables that depend on the source image
		originW = sourceImage.getWidth();
		originH = sourceImage.getHeight();
		makeKernel(blurRadius);
	}
	
	private Bitmap resize(Bitmap origin) {
		/* shrink the bitmap to RESIZE width with matching ratio for height, bitmaps bigger than this
		 * value will be calcuated too slowly */
		
		int w = origin.getWidth();
		ratio = w/RESIZE;
		Bitmap resized = Bitmap.createScaledBitmap(origin, RESIZE, origin.getHeight()/ratio, true);
		width = resized.getWidth();
		height = resized.getHeight();
		return resized;	
	}
	
	//helper function to add padding to bitmap for lost pixels
	private Bitmap pad(Bitmap gray, int padding) {
		//add padding to the image to make up for any edge pixels of a bitmap lost during a calculation
		Bitmap pad = Bitmap.createBitmap(gray.getWidth()+padding, gray.getHeight()+padding, gray.getConfig());
		Canvas c = new Canvas(pad);
		c.drawBitmap(gray, 0, 0, null);
		return pad;
	}
	
	private Bitmap blur(Bitmap in) {
		//pad the image
		Bitmap pad = pad(in, blurRadius);
		
		//apply the kernel to the image to blur
		Bitmap blurred = Bitmap.createBitmap(width, height, pad.getConfig());
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				float sum = 0;
				for (int i = 0; i < blurRadius; i++) {
					for (int j = 0; j < blurRadius; j++) {
						sum += Color.red(pad.getPixel(x+i, y+j)) * kernel[i][j];	
					}
				}
				blurred.setPixel(x, y, Color.rgb((int)sum, (int)sum, (int)sum));
			}
		}
		return blurred;
	}
	
	private Bitmap restore(Bitmap shrunk) {
		//restore the shrunk bitmap to its original size
		Bitmap resized = Bitmap.createScaledBitmap(shrunk, originW, originH, true);
		return resized;	
	}
	
	private void writeData() {
		//write the bitmap information to the global singleton which holds the activity data
		DataHolder.getInstance().setImgBitmap(outputImage);
		//sourceImage.recycle();
	}
}
