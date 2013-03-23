package com.alexdiru.redleaf;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.alexdiru.redleaf.interfaces.IDisposable;
import com.alexdiru.redleaf.interfaces.IRenderable;

/** Represents a bounding box which can be touched They can also be drawn to the screen
 * @author Alex */
public class DataBoundingBox implements IRenderable, IDisposable {

	/** Used for the coordinates of the bounding box */
	private Rect mRect = new Rect();

	/** The paint used to draw the rectangle around the tapboxes */
	private static Paint mRectanglePaint = new Paint();
	
	private Paint mPaint = null;
	
	protected Bitmap mRenderBitmap;
	private int mRectangleWidth = 0;
	
	public DataBoundingBox() {
	}
	
	public DataBoundingBox(Bitmap bitmap) {
		mRenderBitmap = bitmap;
	}
	
	public DataBoundingBox(Bitmap bitmap, Paint paint) {
		this(bitmap);
		mPaint = paint;
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
		//Odd width, make even
		if (width % 2 != 0)
			width++;
		mRectangleWidth = width;
		mRectanglePaint.setColor(Color.BLACK);
		mRectanglePaint.setStrokeWidth(mRectangleWidth);
	}


	@Override
	public void render(Canvas canvas) {

		canvas.drawBitmap(mRenderBitmap, mRect.left, mRect.top, mPaint);

		if (mRectangleWidth > 0) {
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

	@Override
	public void dispose() {
		UtilsDispose.disposeBitmap(mRenderBitmap);
		mRect = null;
		mRectanglePaint = null;
		mPaint = null;
		mRenderBitmap = null;
	}
}
