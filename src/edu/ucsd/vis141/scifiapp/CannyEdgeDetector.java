package edu.ucsd.vis141.scifiapp;

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
	private final static int RESIZE = 150;
	//private final static float GAUSSIAN_CUT_OFF = 0.005f;
	//private final static float MAGNITUDE_SCALE = 100F;
	//private final static float MAGNITUDE_LIMIT = 1000F;
	//private final static int MAGNITUDE_MAX = (int) (MAGNITUDE_SCALE * MAGNITUDE_LIMIT);

	// fields
	private int height, width;
	private int originH, originW, ratio;
	private Bitmap sourceImage;
	private Bitmap outputImage;
	
	private float sigma;
	//private float lowThreshold;
	//private float highThreshold;
	private int blurRadius;
	
	float [][] kernel;
	
// constructors	
    //Constructs a new detector with default parameters.
	public CannyEdgeDetector() {
		//lowThreshold = 2.5f;
		//highThreshold = 7.5f;
		sigma = 2f;
		blurRadius = 6;
	}
	//Constructs a new detector with an image
	public CannyEdgeDetector(Bitmap source) {
		this.sourceImage = source;
		//lowThreshold = 2.5f;
		//highThreshold = 7.5f;
		sigma = 2f;
		blurRadius = 16;
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
		outputImage = toGrayScale(outputImage);
		outputImage = pad(outputImage);
		outputImage = blur(outputImage);
		computeGradients(outputImage);
		hysteresis();
		threshold();
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
	

	private Bitmap pad(Bitmap gray) {
		Bitmap pad = Bitmap.createBitmap(gray.getWidth()+blurRadius, gray.getHeight()+blurRadius, gray.getConfig());
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
		Bitmap findGrad = pad(blurred);
		float [][] sorbelX = new float[][]{ {-1, 0, 1},
				                            {-2, 0, 2},
											{-1, 0, 1}};
		float [][] sorbelY = new float[][]{ {-1, -2, -1},
											{0, 0, 0},
											{1, 2, 1} };
		
		float [][] xGrad = new float[height][width];
		float [][] yGrad = new float[height][width];
		
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
				xGrad[y][x] = sumx;
				yGrad[y][x] = sumy;
			}
		}
		//perform non-maximal suppression
		/*
		 		int index = x + y;
				int indexN = index - width;
				int indexS = index + width;
				int indexW = index - 1;
				int indexE = index + 1;
				int indexNW = indexN - 1;
				int indexNE = indexN + 1;
				int indexSW = indexS - 1;
				int indexSE = indexS + 1;
				
				float xGrad = xGradient[index];
				float yGrad = yGradient[index];
				float gradMag = hypot(xGrad, yGrad);

				//perform non-maximal supression
				float nMag = hypot(xGradient[indexN], yGradient[indexN]);
				float sMag = hypot(xGradient[indexS], yGradient[indexS]);
				float wMag = hypot(xGradient[indexW], yGradient[indexW]);
				float eMag = hypot(xGradient[indexE], yGradient[indexE]);
				float neMag = hypot(xGradient[indexNE], yGradient[indexNE]);
				float seMag = hypot(xGradient[indexSE], yGradient[indexSE]);
				float swMag = hypot(xGradient[indexSW], yGradient[indexSW]);
				float nwMag = hypot(xGradient[indexNW], yGradient[indexNW]);
				float tmp;
				*/
				/*
				 * An explanation of what's happening here, for those who want
				 * to understand the source: This performs the "non-maximal
				 * supression" phase of the Canny edge detection in which we
				 * need to compare the gradient magnitude to that in the
				 * direction of the gradient; only if the value is a local
				 * maximum do we consider the point as an edge candidate.
				 * 
				 * We need to break the comparison into a number of different
				 * cases depending on the gradient direction so that the
				 * appropriate values can be used. To avoid computing the
				 * gradient direction, we use two simple comparisons: first we
				 * check that the partial derivatives have the same sign (1)
				 * and then we check which is larger (2). As a consequence, we
				 * have reduced the problem to one of four identical cases that
				 * each test the central gradient magnitude against the values at
				 * two points with 'identical support'; what this means is that
				 * the geometry required to accurately interpolate the magnitude
				 * of gradient function at those points has an identical
				 * geometry (upto right-angled-rotation/reflection).
				 * 
				 * When comparing the central gradient to the two interpolated
				 * values, we avoid performing any divisions by multiplying both
				 * sides of each inequality by the greater of the two partial
				 * derivatives. The common comparand is stored in a temporary
				 * variable (3) and reused in the mirror case (4).
				 * 
				 */
		/*
				if (xGrad * yGrad <= (float) 0 //(1)
					? Math.abs(xGrad) >= Math.abs(yGrad) //(2)
						? (tmp = Math.abs(xGrad * gradMag)) >= Math.abs(yGrad * neMag - (xGrad + yGrad) * eMag) //(3)
							&& tmp > Math.abs(yGrad * swMag - (xGrad + yGrad) * wMag) //(4)
						: (tmp = Math.abs(yGrad * gradMag)) >= Math.abs(xGrad * neMag - (yGrad + xGrad) * nMag) //(3)
							&& tmp > Math.abs(xGrad * swMag - (yGrad + xGrad) * sMag) //(4)
					: Math.abs(xGrad) >= Math.abs(yGrad) //(2)
						? (tmp = Math.abs(xGrad * gradMag)) >= Math.abs(yGrad * seMag + (xGrad - yGrad) * eMag) //(3)
							&& tmp > Math.abs(yGrad * nwMag + (xGrad - yGrad) * wMag) //(4)
						: (tmp = Math.abs(yGrad * gradMag)) >= Math.abs(xGrad * seMag + (yGrad - xGrad) * sMag) //(3)
							&& tmp > Math.abs(xGrad * nwMag + (yGrad - xGrad) * nMag) //(4)
					) {
					magnitude[index] = gradMag >= MAGNITUDE_LIMIT ? MAGNITUDE_MAX : (int) (MAGNITUDE_SCALE * gradMag);
					//NOTE: The orientation of the edge is not employed by this
					//implementation. It is a simple matter to compute it at
					//this point as: Math.atan2(yGrad, xGrad);
				} else {
					magnitude[index] = 0;
				}
		 */
	}
	
	private void hysteresis() {
		
	}
	
	private void threshold() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (Color.red(outputImage.getPixel(x, y)) < 10) {
					outputImage.setPixel(x, y, Color.BLACK);   //Color.TRANSPARENT
				}
			}
		}
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
