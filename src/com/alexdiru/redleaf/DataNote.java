package com.alexdiru.redleaf;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;

public class DataNote {

	public static final int NOTE_TYPE_TAP = 0;
	public static final int NOTE_TYPE_HOLD = 1;
	
	/** Only need one rectangle to share between all of the notes */
	private static Rect mHoldLineRect = new Rect();

	private int mStartTime;
	private int mEndTime;
	private int mType; //Unused for now
	private int mPosition;
	private boolean mTapped;
	public int mTopY;
	public int mBottomY;

	/** If a hold note, whether the note is being held down */
	private boolean mBeingHeld;

	public DataNote(int startTime, int endTime, int type, int position) {
		mStartTime = startTime;
		mEndTime = endTime;
		mType = type;
		mPosition = position;
	}

	public DataNote(String[] fields) {
		mStartTime = Integer.parseInt(fields[1]);
		mEndTime = Integer.parseInt(fields[2]);
		mType = Integer.parseInt(fields[3]);
		mPosition = Integer.parseInt(fields[4]);
		mTapped = false;
		mBeingHeld = false;
	}

	public boolean isHit(int tapBoxTop, int tapBoxBottom) {
		return (mBottomY > tapBoxTop && mBottomY < tapBoxBottom) || (mTopY > tapBoxTop && mTopY < tapBoxBottom);
	}
	
	public boolean isHoldNote() {
		return mEndTime != 0;
	}

	public boolean isTapNote() {
		return mEndTime == 0;
	}
	
	public boolean hasBeenTapped() {
		return mTapped;
	}
	
	public int getStartTime() {
		return mStartTime;
	}
	
	public int getEndTime() {
		return mEndTime;
	}
	
	public int getPosition() {
		return mPosition;
	}
	
	public void setHeld(boolean beingHeld) {
		mBeingHeld = beingHeld;
	}
	
	public boolean isHeld() {
		return mBeingHeld;
	}

	public void drawHoldLine(Canvas canvas, Paint held, Paint unheld, int noteX) {
		// Get the top of the hold line
		int holdLineYPosition = Utils.getCurrentSong().mMusicManager.getPlayPosition() - mEndTime + GameView.TAPCIRCLES_Y;

		// Top of line cannot be negative
		if (holdLineYPosition < 0)
			holdLineYPosition = 0;
		
		if (mHoldLineRect == null)
			return;
		
		mHoldLineRect.left = noteX;// + DataSong.NOTESIZE / 4;
		mHoldLineRect.top = holdLineYPosition;
		mHoldLineRect.right = noteX + DataSong.NOTESIZE;//3 * (DataSong.NOTESIZE / 4);
		mHoldLineRect.bottom = mBottomY - DataSong.NOTESIZE/2;
		
		//mHoldLinePath.reset();
		//mHoldLinePath.addRect(mHoldLineRect, Direction.CCW);
		
		canvas.drawRect(mHoldLineRect, mBeingHeld ? held : unheld);
	}

	public void setTapped(boolean tapped) {
		mTapped = tapped;
	}
}
