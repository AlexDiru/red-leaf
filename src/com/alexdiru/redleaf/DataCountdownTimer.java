package com.alexdiru.redleaf;

import android.graphics.Canvas;

import com.alexdiru.redleaf.android.StrokePaint;
import com.alexdiru.redleaf.interfaces.IRenderable;

public class DataCountdownTimer implements IRenderable {
	
	private StrokePaint mPaint;
	private int mTime;
	
	public DataCountdownTimer(StrokePaint paint) {
		mTime = 3000;
		mPaint = paint;
	}
	
	public void update(long elapsedTime) {
		mTime -= (int)elapsedTime;
	}

	@Override
	public void render(Canvas canvas) {
		StringBuilder sb = UtilsString.getStringBuilder();
		UtilsString.appendInteger((mTime/1000)+1);
		mPaint.drawText(canvas, sb.toString(), UtilsScreenSize.getScreenWidth() >> 1, UtilsScreenSize.getScreenHeight() >> 1);
	}
	
	public boolean hasFinished() {
		return mTime <= 0;
	}
}
