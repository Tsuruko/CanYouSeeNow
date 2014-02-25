package edu.ucsd.vis141.SciFiApp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/*********************************
 * UCSD VIS 141A project
 * SciFiAPP
 * 
 * Created By: Monica Liu
 * Last Modified 2/10/14
 * 
 * CannyEdgeDetector.java: 
 *   An implementation of a canny edge detector on bitmaps.
 *   
 ********************************/

public class CannyEdgeDetector {

	// statics
	private final static int RESIZE = 200;
	private final static float lowerThreshold = 20f;
	private final static float upperThreshold = 1000f;

	// fields
	private int height, width;
	private int originH, originW, ratio;
	private Bitmap sourceImage;
	private Bitmap outputImage;
	
	private float sigma;
	private int blurRadius;
	//private float lowThreshold;
	//private float highThreshold;
	
	float [][] kernel;
	float [][] output;
	
// constructors	
    //Constructs a new detector with default parameters.
	public CannyEdgeDetector() {
		//lowThreshold = 2.5f;
		//highThreshold = 7.5f;
		sigma = 1f;
		blurRadius = 4;
	}
	//Constructs a new detector with an image
	public CannyEdgeDetector(Bitmap source) {
		this.sourceImage = source;
		//lowThreshold = 2.5f;
		//highThreshold = 7.5f;
		sigma = 1f;
		blurRadius = 6;
	}
	
	public void setSourceImage(Bitmap m) {
		if (sourceImage == null) sourceImage = m;
		else {
			sourceImage.recycle();
			sourceImage = m;
		}
	}
	public void findEdges() {
		initialize();
		outputImage = resize(sourceImage);
		outputImage = changeContrastBrightness(outputImage, 10, 0);
		outputImage = toGrayScale(outputImage);
		outputImage = pad(outputImage, blurRadius);
		outputImage = blur(outputImage);
		computeGradients(outputImage);
		hysteresis();
		outputImage = threshold();
		outputImage = restore(outputImage);
		writeData();
	}
	
	private void makeKernel(int radius) {
		//create the gaussian filter kernel
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
		originW = sourceImage.getWidth();
		originH = sourceImage.getHeight();
		makeKernel(blurRadius);
	}
	
	
	private Bitmap resize(Bitmap origin) {
		int w = origin.getWidth();
		ratio = w/RESIZE;
		Bitmap resized = Bitmap.createScaledBitmap(origin, RESIZE, origin.getHeight()/ratio, true);
		width = resized.getWidth();
		height = resized.getHeight();
		return resized;	
	}
	
	private Bitmap toGrayScale(Bitmap bmpOriginal) {
		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}
	
	private Bitmap changeContrastBrightness(Bitmap bmp, float contrast, float brightness)
	{
	    ColorMatrix cm = new ColorMatrix(new float[]
	            {
	                contrast, 0, 0, 0, brightness,
	                0, contrast, 0, 0, brightness,
	                0, 0, contrast, 0, brightness,
	                0, 0, 0, 1, 0
	            });

	    Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

	    Canvas canvas = new Canvas(ret);

	    Paint paint = new Paint();
	    paint.setColorFilter(new ColorMatrixColorFilter(cm));
	    canvas.drawBitmap(bmp, 0, 0, paint);

	    return ret;
	}
	

	private Bitmap pad(Bitmap gray, int padding) {
		Bitmap pad = Bitmap.createBitmap(gray.getWidth()+padding, gray.getHeight()+padding, gray.getConfig());
		Canvas c = new Canvas(pad);
		c.drawBitmap(gray, 0, 0, null);
		return pad;
	}
	
	private Bitmap blur(Bitmap pad) {
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
	
	private void computeGradients(Bitmap blurred) { 
		Bitmap findGrad = pad(blurred, 3);
		float [][] sorbelX = new float[][]{ {-1, 0, 1},
				                            {-2, 0, 2},
											{-1, 0, 1}};
		float [][] sorbelY = new float[][]{ {-1, -2, -1},
											{0, 0, 0},
											{1, 2, 1} };
		
		float [][] xGrad = new float[width][height];
		float [][] yGrad = new float[width][height];
		float [][] mag = new float[width][height];
		output = new float[width][height];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				float sumx = 0;
				float sumy = 0;
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						sumx += Color.red(findGrad.getPixel(x+i, y+j)) * sorbelX[i][j];
						sumy += Color.red(findGrad.getPixel(x+i, y+j)) * sorbelY[i][j];
					}
				}
				xGrad[x][y] = sumx;
				yGrad[x][y] = sumy;
				mag[x][y] = (float) Math.sqrt((sumx * sumx) + (sumy * sumy));
			}
		}
		findGrad.recycle();

		for (int i = 0; i < width; i++) {
			output[i][0] = 0;
			output[i][height-1] = 0;
		}
		for (int i = 0; i < height; i++) {
			output[0][i] = 0;
			output[width-1][0] = 0;
		}
		
		//non-maximal suppression
		for (int y = 1; y < height-1; y++)  {
			for (int x = 1; x < width-1; x++) {
				/*
				int dx, dy;
				if (xGrad[x][y] > 0) dx = 1;
				else dx = -1;
				if (yGrad[x][y] > 0) dy = 1;
				else dy = -1;
				
				float a1, a2, b1, b2, A, B, point, val;
				if (Math.abs(xGrad[x][y]) > Math.abs(yGrad[x][y])) {
					a1 = mag[x+dx][y];
					a2 = mag[x+dx][y-dy];
					b1 = mag[x-dx][y];
					b2 = mag[x-dx][y+dy];
					A = (float) (Math.abs(xGrad[x][y]) - Math.abs(yGrad[x][y]))*a1 + Math.abs(yGrad[x][y])*a2;
					B = (float) (Math.abs(xGrad[x][y]) - Math.abs(yGrad[x][y]))*b1 + Math.abs(yGrad[x][y])*b2;
					point = mag[x][y] * Math.abs(xGrad[x][y]);
					if (point >= A && point > B) {
						val = Math.abs(xGrad[x][y]);
						output[x][y] = 0xff000000 | ((int)(val) << 16 | (int)(val) << 8 | (int)(val));
					} else {
						val = 0;
						output[x][y] = 0xff000000;
					}
				} else {
					a1 = mag[x][y-dy];
					a2 = mag[x+dx][y-dy];
					b1 = mag[x][y+dy];
					b2 = mag[x-dx][y+dy];						
					A = (Math.abs(yGrad[x][y]) - Math.abs(xGrad[x][y]))*a1 + Math.abs(xGrad[x][y])*a2;
					B = (Math.abs(yGrad[x][y]) - Math.abs(xGrad[x][y]))*b1 + Math.abs(xGrad[x][y])*b2;
					point = mag[x][y] * Math.abs(yGrad[x][y]);
					if(point >= A && point > B) {
						val = Math.abs(yGrad[x][y]);
						output[x][y] = 0xff000000 | ((int)(val) << 16 | (int)(val ) << 8 | (int)(val));
					}
					else {
						val = 0;
						output[x][y] = 0xff000000;
					}							
				}
				*/
				double angle = Math.atan2(yGrad[x][y], xGrad[x][y]);
				//round to 0 deg
				if ((angle >= 0 && angle < Math.PI/8) || (angle > 7*Math.PI/8 && angle < Math.PI)) {
					if (!(mag[x][y] > mag[x][y+1] && mag[x][y] > mag[x][y-1])) output[x][y] = 0;
					else output[x][y] = mag[x][y];
				}
				//round to 45 deg
				if (angle >= Math.PI/8 || angle < 3*Math.PI/8) {
					if (!(mag[x][y] > mag[x-1][y-1] && mag[x][y] > mag[x+1][y+1])) output[x][y] = 0;
					else output[x][y] = mag[x][y];
				}
				//round to 90 deg
				if (angle >= 3*Math.PI/8 || angle < 5*Math.PI/8) {
					if (!(mag[x][y] > mag[x+1][y] && mag[x][y] > mag[x-1][y])) output[x][y] = 0;
					else output[x][y] = mag[x][y];
				}
				//round to 135 deg
				if (angle >= 5*Math.PI/8 || angle < 7*Math.PI/8) {
					if (!(mag[x][y] > mag[x+1][y-1] && mag[x][y] > mag[x-1][y+1])) output[x][y] = 0;
					else output[x][y] = mag[x][y];
				}
			}
		}
	}
	
	private void hysteresis() {	
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				int value = ((int)output[x][y]) & 0xff; 
				if (value >= upperThreshold) {
					output[x][y] = 0xffffffff;
					hystConnect(x, y);
				}
			}
		}
	}
	
	private void hystConnect(int x, int y) {
		int value = 0;
		for (int x1=x-1;x1<=x+1;x1++) {
			for (int y1=y-1;y1<=y+1;y1++) {
				if ((x1 < width) & (y1 < height) & (x1 >= 0) & (y1 >= 0) & (x1 != x) & (y1 != y)) {
					value = ((int)output[x1][y1]) & 0xff;
					if (value != 255) {
						if (value >= lowerThreshold) {
							output[x1][y1] = 0xffffffff;
							hystConnect(x1, y1);
						} 
						else {
							output[x1][y1] = 0xff000000;
						}
					}
				}
			}
		}

	}
	
	private Bitmap threshold() {
		Bitmap edges = Bitmap.createBitmap(width, height, outputImage.getConfig());
		Bitmap finishedEdges = Bitmap.createBitmap(width, height, sourceImage.getConfig());
		Paint paint = new Paint();
		paint.setAlpha(100);
		Canvas canvas = new Canvas(finishedEdges);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (output[x][y] < 7) {
					output[x][y] = 0;
				}
				if (output[x][y] == 0) {
					edges.setPixel(x, y, Color.WHITE); 
				} else edges.setPixel(x, y, Color.TRANSPARENT);
			}
		}
		canvas.drawBitmap(edges, 0, 0, paint);
		return finishedEdges;
	}
	
	private Bitmap restore(Bitmap shrunk) {
		Bitmap resized = Bitmap.createScaledBitmap(shrunk, originW, originH, true);
		return resized;	
	}
	
	private void writeData() {
		DataHolder.getInstance().setBitmap(outputImage);
		DataHolder.getInstance().setStatus();
		//sourceImage.recycle();
	}
}
