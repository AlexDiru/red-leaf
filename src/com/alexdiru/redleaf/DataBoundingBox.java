package com.alexdiru.redleaf;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/** Represents a bounding box which can be touched They can also be drawn to the screen
 * @author Alex */
public class DataBoundingBox {

	/** Used for the coordinates of the bounding box */
	private Rect mRect = new Rect();

	/** The paint used to draw the rectangle around the tapboxes */
	private static Paint mRectanglePaint = new Paint();

	/** Updates the position of the bounding box */
	public void update(int x1, int y1, int x2, int y2) {
		mRect.left = x1;
		mRect.right = x2;
		mRect.top = y1;
		mRect.bottom = y2;
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

	/** Draw the bounding box as a circle - used for drawing the tap areas
	 * @param canvas The canvas to draw to
	 * @param paint The paint to draw the circle with */
	public void drawAsCircle(Canvas canvas, Paint paint) {
		canvas.drawCircle(mRect.left + ((mRect.right - mRect.left) / 2), mRect.top + ((mRect.bottom - mRect.top) / 2), ((mRect.right - mRect.left) / 2), paint);
	}

	/** Draw the bounding box as a rectangle - used for drawing the tap areas
	 * @param canvas The canvas to draw to
	 * @param paint The paint to draw the rectangle with */
	public void drawAsRect(Canvas canvas, Paint paint) {
		canvas.drawRect(mRect, paint);
	}

	/** Draws the bounding box by using a bitmap
	 * @param canvas The canvas to draw to
	 * @param bitmap The bitmap to use to display the bounding box
	 * @param paint The paint to draw the bitmap with
	 * @param drawSurroundingRectangle Whether to draw a rectangle which surrounds the bitmap
	 * @param rectWidth The width of the lines of the rectangle (if one is drawn) */
	public void drawWithBitmap(Canvas canvas, Bitmap bitmap, Paint paint, boolean drawSurroundingRectangle, int rectWidth) {
		mRectanglePaint.setColor(Color.BLACK);
		mRectanglePaint.setStrokeWidth(rectWidth);

		canvas.drawBitmap(bitmap, mRect.left, mRect.top, paint);

		if (drawSurroundingRectangle) {
			canvas.drawLine(mRect.left - rectWidth, mRect.top - rectWidth / 2, mRect.right + rectWidth, mRect.top - rectWidth / 2, mRectanglePaint);
			canvas.drawLine(mRect.left - rectWidth, mRect.bottom + rectWidth / 2, mRect.right + rectWidth, mRect.bottom + rectWidth / 2, mRectanglePaint);
			canvas.drawLine(mRect.left - rectWidth / 2, mRect.top, mRect.left - rectWidth / 2, mRect.bottom, mRectanglePaint);
			canvas.drawLine(mRect.right + rectWidth / 2, mRect.top, mRect.right + rectWidth / 2, mRect.bottom, mRectanglePaint);

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
