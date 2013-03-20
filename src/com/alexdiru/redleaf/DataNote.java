package com.alexdiru.redleaf;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/** Represents a note which appears on a song i.e. falls down from the top of the screen and the
 * player must tap it
 * @author Alex */
public class DataNote {

	public static final int NOTE_TYPE_TAP = 0;
	public static final int NOTE_TYPE_HOLD = 1;
	public static final int NOTE_TYPE_TAP_STAR = 2;
	public static final int NOTE_TYPE_HOLD_STAR = 3;

	/** Only need one rectangle to share between all of the notes */
	private static Rect mHoldLineRect = new Rect();

	/** The ms time at which the note appears in the song */
	private int mStartTime;

	/** The ms time at which the note (if hold) ends in the song */
	private int mEndTime;

	/** The type of the note Tap, Hold */
	private int mType; // Unused for now

	/** The position at which the note appears - left, centre left, centre right, right */
	private int mPosition;

	/** Whether the note has been tapped */
	private boolean mTapped;

	/** Stores the pixel position of the top of the note when it is being rendered */
	public int mTopY;

	/** Stores the pixel position of the bottom of the note when it is being rendered */
	public int mBottomY;

	/** If a hold note, whether the note is being held down */
	private boolean mBeingHeld;

	/** Creates the note
	 * @param startTime The time at which the note starts
	 * @param endTime The time at which the note ends
	 * @param type The type the note is
	 * @param position The position of the notes */
	public DataNote(int startTime, int endTime, int type, int position) {
		mStartTime = startTime;
		mEndTime = endTime;
		mType = type;
		mPosition = position;
		mTapped = false;
		mBeingHeld = false;
	}

	/** Create a note based of fields loaded from a file
	 * @param fields The fields loaded from a file */
	public DataNote(String[] fields) {
		this(Integer.parseInt(fields[1]), Integer.parseInt(fields[2]), Integer.parseInt(fields[3]), Integer.parseInt(fields[4]));
	}

	/** Whether the note is hit at the current point in the song
	 * @param currentTime The current time of the song
	 * @param tapWindow The time period at which the note can be tapped
	 * @return Whether the note was hit */
	public boolean isHit(int currentTime, int tapWindow) {
		return (mStartTime >= currentTime - tapWindow && mStartTime <= currentTime + tapWindow);
	}

	public void draw(Canvas canvas, DataPlayer tapAreas, float songSpeed) {
		// Update note coordinates
		mTopY = (int) ((Utils.getCurrentSong().mMusicManager.getPlayPosition() - getStartTime()) * songSpeed) + GameView.TAPCIRCLES_Y;
		mBottomY = mTopY + DataSong.NOTESIZE;

		int noteXPosition = tapAreas.getBoundingBoxLeft(mPosition) + (UtilsScreenSize.scaleX(DataPlayer.TAP_AREA_WIDTH - DataSong.NOTESIZE) >> 1);

		if (isHoldNote())
			drawHoldLine(canvas, tapAreas.getColourSchemeAssets().getHoldLineHeldPaint(mPosition), tapAreas.getColourSchemeAssets().getHoldLineUnheldPaint(mPosition), noteXPosition, songSpeed);
		
		if (isStarNote())
			canvas.drawBitmap(isHeld() && isHoldNote() ? tapAreas.getColourSchemeAssets().getNoteHeld(mPosition) : tapAreas.getColourSchemeAssets().getNoteStar(mPosition), noteXPosition, mTopY, null);
		else
			canvas.drawBitmap(isHeld() && isHoldNote() ? tapAreas.getColourSchemeAssets().getNoteHeld(mPosition) : tapAreas.getColourSchemeAssets().getNote(mPosition), noteXPosition, mTopY, null);

	}

	private void drawHoldLine(Canvas canvas, Paint held, Paint unheld, int noteX, float songSpeed) {
		// Get the top of the hold line
		int holdLineYPosition = (int) ((Utils.getCurrentSong().mMusicManager.getPlayPosition() - mEndTime) * songSpeed) + GameView.TAPCIRCLES_Y;// - (int)(DataSong.NOTESIZE * 1.5);

		// Top of line cannot be negative
		if (holdLineYPosition < 0)
			holdLineYPosition = 0;

		if (mHoldLineRect == null)
			return;

		mHoldLineRect.left = noteX;
		mHoldLineRect.top = UtilsScreenSize.scaleY(holdLineYPosition);
		mHoldLineRect.right = noteX + UtilsScreenSize.scaleY(DataSong.NOTESIZE);
		mHoldLineRect.bottom = UtilsScreenSize.scaleY(mBottomY - DataSong.NOTESIZE / 2);

		canvas.drawRect(mHoldLineRect, mBeingHeld ? held : unheld);
	}

	public boolean isHoldNote() {
		return mEndTime != 0;
	}

	public boolean isTapNote() {
		return mEndTime == 0;
	}

	public boolean isStarNote() {
		return mType == NOTE_TYPE_TAP_STAR || mType == NOTE_TYPE_HOLD_STAR;
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
	
	public int getType() {
		return mType;
	}

	public void setHeld(boolean beingHeld) {
		mBeingHeld = beingHeld;
	}

	public boolean isHeld() {
		return mBeingHeld;
	}

	public void setTapped(boolean tapped) {
		mTapped = tapped;
	}
}
