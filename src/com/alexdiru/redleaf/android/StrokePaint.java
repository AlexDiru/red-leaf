package com.alexdiru.redleaf.android;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;

import com.alexdiru.redleaf.interfaces.IDisposable;

public class StrokePaint implements IDisposable {

	private Paint mPaint;
	private Paint mBackgroundPaint;
	
	public StrokePaint() {
		mPaint = new Paint();
		mBackgroundPaint = new Paint();
		
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
	}

	public void setARGB(int a, int r, int g, int b) {
		mPaint.setARGB(a, r, g, b);
	}
	
	public void setStrokeARGB(int a, int r, int g, int b) {
		mBackgroundPaint.setARGB(a, r, g, b);
	}
	
	public void setTypeface(Typeface typeface) {
		mPaint.setTypeface(typeface);
		mBackgroundPaint.setTypeface(typeface);
	}
	
	public void setTextAlign(Align align) {
		mPaint.setTextAlign(align);
		mBackgroundPaint.setTextAlign(align);
	}
	
	public void setTextSize(int size) {
		mPaint.setTextSize(size);
		mBackgroundPaint.setTextSize(size);
	}
	
	public void setStrokeWidth(float width) {
		mBackgroundPaint.setStrokeWidth(width);
	}

	public void drawText(Canvas canvas, String text, float x, float y) {
		canvas.drawText(text, x, y, mBackgroundPaint);
		canvas.drawText(text, x, y, mPaint);
	}
	
	public void drawText(Canvas canvas, char[] text, int start, int length, float x, float y) {
		canvas.drawText(text, start, length, x, y, mBackgroundPaint);
		canvas.drawText(text, start, length, x, y, mPaint);
	}

	@Override
	public void dispose() {
		mPaint = null;
		mBackgroundPaint = null;
	}
}
