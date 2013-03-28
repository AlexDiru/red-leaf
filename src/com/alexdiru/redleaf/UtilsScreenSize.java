package com.alexdiru.redleaf;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Display;

import com.alexdiru.redleaf.exception.ResourceNotExistant;

public abstract class UtilsScreenSize {
	
	private static int mScreenHeight;
	private static int mScreenWidth;
	private static float mScaleX;
	private static float mScaleY;
	
	@SuppressWarnings("deprecation")
	public static void initialise(Display display) {
		
		mScreenHeight = display.getHeight();
		mScreenWidth = display.getWidth();
		
		mScaleX = (float)mScreenWidth/720f;
		mScaleY = (float)mScreenHeight/1280f;
	}
	
	public static int getScreenHeight() {
		return mScreenHeight;
	}
	
	public static int getScreenWidth() {
		return mScreenWidth;
	}
	
	public static float getScaleX() {
		return mScaleX;
	}
	
	public static float getScaleY() {
		return mScaleY;
	}
	
	public static int scaleX(int px) {
		return (int)(px * mScaleX);
	}

	public static int scaleY(int px) {
		return (int)(px * mScaleY);
	}
	
	public static int scaleX(float px) {
		return (int)(px * mScaleX);
	}
	
	public static int scaleY(float py) {
		return (int)(py * mScaleY);
	}
	
	public static int scaleFontSize(int sz) {
		return (int)(sz * mScaleY);
	}
	
	public static int getHeightInRatio(int newWidth, int originalWidth, int originalHeight) {
		return (int)((float)newWidth * (originalHeight/(float)originalWidth));
	}

	public static int getWidthInRatio(int newHeight, int originalWidth, int originalHeight) {
		return (int)((float)newHeight * (originalWidth/(float)originalHeight));
	}
	
	public static Bitmap loadBitmapInRatioFromWidth(String assetFile, int newWidth, int originalWidth, int originalHeight) {
		try {
			return loadBitmapInRatioFromWidth(BitmapFactory.decodeStream(Utils.getActivity().getAssets().open(assetFile)), newWidth, originalWidth, originalHeight);
		} catch (IOException e) {
			throw new ResourceNotExistant(assetFile + " does not exist");
		}
	}
	
	public static Bitmap loadBitmapInRatioFromHeight(String assetFile, int newHeight) {
		try {
			Bitmap original = BitmapFactory.decodeStream(Utils.getActivity().getAssets().open(assetFile));
			return loadBitmapInRatioFromHeight(original, newHeight, original.getWidth(), original.getHeight());
		} catch (IOException e) {
			throw new ResourceNotExistant(assetFile + " does not exist");
		}
	}
	
	public static Bitmap loadBitmapInRatioFromWidth(Bitmap src, int newWidth, int originalWidth, int originalHeight) {
		int newHeight = getHeightInRatio(newWidth, originalWidth, originalHeight);
		return Bitmap.createScaledBitmap(src, newWidth, newHeight, false);
	}
	
	public static Bitmap loadBitmapInRatioFromHeight(Bitmap src, int newHeight, int originalWidth, int originalHeight) {
		int newWidth = getWidthInRatio(newHeight, originalWidth, originalHeight);
		return Bitmap.createScaledBitmap(src, newWidth, newHeight, false);
	}
}
