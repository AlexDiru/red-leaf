package com.alexdiru.redleaf;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class DataBoundingBox {

	/**
	 * Used for the coordinates of the bounding box
	 */
	private Rect mRect = new Rect();
	
	private static Paint mRectanglePaint = new Paint();

	/**
	 * Updates the position of the bounding box
	 */
	public void update(int x1, int y1, int x2, int y2) {
		mRect.left = x1;
		mRect.right = x2;
		mRect.top = y1;
		mRect.bottom = y2;
	}
	
	/**
	 * Whether the entered coordinates are inside the bounding box
	 */
	public boolean isTouched(int x, int y, int extensionX, int extensionY) {
		return (x >= mRect.left - extensionX && x <= mRect.right + extensionX && y >= mRect.top - extensionY && y <= mRect.bottom + extensionY);
	}

	/**
	 * Draw the bounding box as a circle - used for drawing the tap areas
	 */
	public void drawAsCircle(Canvas canvas, Paint paint) {
		canvas.drawCircle(mRect.left + ((mRect.right - mRect.left) / 2), mRect.top + ((mRect.bottom - mRect.top) / 2), ((mRect.right - mRect.left) / 2), paint);
	}
	
	public void drawAsRect(Canvas canvas, Paint paint) {
		canvas.drawRect(mRect, paint);
	}
	
	public void drawWithBitmap(Canvas canvas, Bitmap bitmap, Paint paint, boolean drawSurroundingRectangle, int rectWidth) {
		mRectanglePaint.setColor(Color.BLACK);
		mRectanglePaint.setStrokeWidth(rectWidth);
		
		canvas.drawBitmap(bitmap, mRect.left,mRect.top, paint);
		
		if (drawSurroundingRectangle) {
			canvas.drawLine(mRect.left - rectWidth, mRect.top - rectWidth/2, mRect.right + rectWidth, mRect.top - rectWidth/2, mRectanglePaint);
			canvas.drawLine(mRect.left - rectWidth, mRect.bottom + rectWidth/2, mRect.right + rectWidth, mRect.bottom + rectWidth/2, mRectanglePaint);
			canvas.drawLine(mRect.left - rectWidth/2, mRect.top, mRect.left - rectWidth/2, mRect.bottom, mRectanglePaint);
			canvas.drawLine(mRect.right + rectWidth/2, mRect.top, mRect.right + rectWidth/2, mRect.bottom, mRectanglePaint);
			
		}
	}
	
	public int getTop() {
		return mRect.top;
	}
	
	public int getBottom() {
		return mRect.bottom;
	}

	public int getLeft() {
		return mRect.left;
	}
}
