package com.alexdiru.redleaf;

import com.alexdiru.redleaf.interfaces.IRenderable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/** Represents a bounding box which can be touched They can also be drawn to the screen
 * @author Alex */
public class DataBoundingBox implements IRenderable {

	/** Used for the coordinates of the bounding box */
	private Rect mRect = new Rect();

	/** The paint used to draw the rectangle around the tapboxes */
	private static Paint mRectanglePaint = new Paint();
	
	protected Bitmap mRenderBitmap;
	private int mRectangleWidth = 0;
	
	public DataBoundingBox() {
	}
	
	public DataBoundingBox(Bitmap bitmap) {
		mRenderBitmap = bitmap;
	}

	/** Updates the position of the bounding box */
	public void update(int x1, int y1, int x2, int y2) {
		mRect.left = x1;
		mRect.right = x2;
		mRect.top = y1;
		mRect.bottom = y2;
	}
	
	public boolean isTouched(int x, int y) {
		return isTouched(x,y,0,0);
	}

	/** Whether the entered coordinates are inside the bounding box
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param extensionX An X extension on the bounding box, applied to both directions
	 * @param extensionY An Y extension on the bounding box, applied to both directions
	 * @return Whether the bounding box was touched */
	public boolean isTouched(int x, int y, int extensionX, int extensionY) {
		return (x >= mRect.left - extensionX && x <= mRect.right + extensionX && y >= mRect.top - extensionY && y <= mRect.bottom + extensionY);
	}
	
	public void setRenderBitmap(Bitmap bitmap) {
		mRenderBitmap = bitmap;
	}
	
	public void setRectangleWidth(int width) {
		mRectangleWidth = width;
	}


	@Override
	public void render(Canvas canvas) {

		canvas.drawBitmap(mRenderBitmap, mRect.left, mRect.top, null);

		if (mRectangleWidth > 0) {
			mRectanglePaint.setColor(Color.BLACK);
			mRectanglePaint.setStrokeWidth(mRectangleWidth);
			canvas.drawLine(mRect.left - mRectangleWidth, mRect.top - mRectangleWidth / 2, mRect.right + mRectangleWidth, mRect.top - mRectangleWidth / 2, mRectanglePaint);
			canvas.drawLine(mRect.left - mRectangleWidth, mRect.bottom + mRectangleWidth / 2, mRect.right + mRectangleWidth, mRect.bottom + mRectangleWidth / 2, mRectanglePaint);
			canvas.drawLine(mRect.left - mRectangleWidth / 2, mRect.top, mRect.left - mRectangleWidth / 2, mRect.bottom, mRectanglePaint);
			canvas.drawLine(mRect.right + mRectangleWidth / 2, mRect.top, mRect.right + mRectangleWidth / 2, mRect.bottom, mRectanglePaint);

		}
	}

	/** Gets the top of the bounding box
	 * @return The top of the bounding box */
	public int getTop() {
		return mRect.top;
	}

	/** Gets the bottom of the bounding box
	 * @return The bottom of the bounding box */
	public int getBottom() {
		return mRect.bottom;
	}

	/** Gets the left of the bounding box
	 * @return The left of the bounding box */
	public int getLeft() {
		return mRect.left;
	}
}
