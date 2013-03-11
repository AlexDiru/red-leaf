package com.alexdiru.redleaf;

import android.graphics.Point;
import android.view.Display;

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
}
