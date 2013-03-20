package com.alexdiru.redleaf;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.alexdiru.redleaf.interfaces.IRenderable;

public class DataTapBox extends DataBoundingBox implements IRenderable {

	private Bitmap mUnheldBitmap;
	private Bitmap mHeldBitmap;
	private boolean mIsHeld = false;
	
	public void setUnheldBitmap(Bitmap bitmap) {
		mUnheldBitmap = bitmap;
	}
	
	public void setHeldBitmap(Bitmap bitmap) {
		mHeldBitmap = bitmap;
	}
	
	public void hold() {
		mIsHeld = true;
	}
	
	public void unhold() {
		mIsHeld = false;
	}
	
	@Override
	public void render(Canvas canvas) {
		if (mIsHeld)
			setRenderBitmap(mHeldBitmap);
		else 
			setRenderBitmap(mUnheldBitmap);
		
		super.render(canvas);
	}
	
}
