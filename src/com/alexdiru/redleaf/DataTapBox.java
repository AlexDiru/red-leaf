package com.alexdiru.redleaf;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.alexdiru.redleaf.interfaces.IDisposable;
import com.alexdiru.redleaf.interfaces.IRenderable;

public class DataTapBox extends DataBoundingBox implements IRenderable, IDisposable {

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
	
	public void update(int x1, int y1) {
		super.update(x1, y1, x1 + mHeldBitmap.getWidth(), y1 + mHeldBitmap.getHeight());
	}
	
	@Override
	public void dispose() {
		super.dispose();
		UtilsDispose.disposeBitmap(mUnheldBitmap);
		UtilsDispose.disposeBitmap(mHeldBitmap);
	}
	
}
