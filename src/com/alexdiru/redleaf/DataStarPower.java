package com.alexdiru.redleaf;

import android.graphics.Canvas;

import com.alexdiru.redleaf.interfaces.IRenderable;
import com.alexdiru.redleaf.interfaces.IDisposable;

public class DataStarPower implements IRenderable, IDisposable {

	private static int mDuration = 10000;
	
	private int mNoteStreak = 0;
	private int mNumberAvailable = 0;
	private boolean mActive = false;
	private DataBoundingBox mBoundingBox;
	private int mTimeOfActivation;
	
	public DataStarPower() {
		mBoundingBox = new DataBoundingBox(ColourSchemeAssets.getStarPower());
		mBoundingBox.update(UtilsScreenSize.getScreenWidth() / 2 - ColourSchemeAssets.getStarPower().getWidth() / 2,
				UtilsScreenSize.getScreenHeight() / 2, UtilsScreenSize.getScreenWidth() / 2 + ColourSchemeAssets.getStarPower().getWidth() / 2,
				UtilsScreenSize.getScreenHeight() / 2 + ColourSchemeAssets.getStarPower().getHeight());
	}
	

	public void update(int currentTime) {
		if (mActive && mTimeOfActivation + mDuration < currentTime)
			end();
	}

	public void handleTouch(int x, int y, int currentTime) {
		if (mNumberAvailable > 0)
			if (!mActive)
				if (mBoundingBox.isTouched(x, y))
					start(currentTime);
	}
	

	private void start(int currentTime) {
		mActive = true;
		mNumberAvailable--;
		mTimeOfActivation = currentTime;
	}

	private void end() {
		mActive = false;
	}

	
	@Override
	public void render(Canvas canvas) {
		if (mNumberAvailable > 0) 
			mBoundingBox.render(canvas);
	}


	public boolean isActive() {
		return mActive;
	}


	public void increaseStreak() {
		mNoteStreak++;
	}
	
	public void increaseNumberAvailable() {
		mNumberAvailable++;
	}


	public int getStreak() {
		return mNoteStreak;
	}


	public void resetStreak() {
		mNoteStreak = 0;
	}


	@Override
	public void dispose() {
		UtilsDispose.dispose(mBoundingBox);
		mBoundingBox = null;
	}


}
