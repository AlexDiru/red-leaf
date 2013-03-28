package com.alexdiru.redleaf;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.alexdiru.redleaf.exception.ValueNotSetException;
import com.alexdiru.redleaf.interfaces.IDisposable;
import com.alexdiru.redleaf.interfaces.IRenderable;

/** Represents a note which appears on a song i.e. falls down from the top of the
 * screen and the player must tap it
 * 
 * @author Alex */
public class DataNote implements Comparable<DataNote>, IDisposable, IRenderable{

	public static final int NOTE_TYPE_TAP = 0;
	public static final int NOTE_TYPE_HOLD = 1;
	public static final int NOTE_TYPE_TAP_STAR = 2;
	public static final int NOTE_TYPE_HOLD_STAR = 3;

	/** The pixel height of each note */
	public static int mNotePixelHeight = -1;

	/** The millisecond time at which the note appears in the song */
	private int mStartTime;

	/** The millisecond time at which the note (if hold) ends in the song */
	private int mEndTime;

	/** The type of the note Tap, Hold */
	private int mType;

	/** The position at which the note appears - left, centre left, centre right,
	 * right */
	private int mPosition;

	/** Whether the note has been tapped */
	private boolean mTapped;

	/** Stores the pixel position of the top of the note when it is being
	 * rendered */
	public int mTopY;

	/** Stores the pixel position of the bottom of the note when it is being
	 * rendered */
	public int mBottomY;

	/** Store the pixel position of the left of the note when it is being
	 * rendered */
	private int mLeft;

	/** If a hold note, whether the note is being held down */
	private boolean mBeingHeld;

	private DataNoteHoldLine mHoldLine;
	
	/** Creates the note
	 * 
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
		
		if (isHoldNote())
			mHoldLine = new DataNoteHoldLine(this);
	}

	/** Create a note based of fields loaded from a file
	 * 
	 * @param fields The fields loaded from a file */
	public DataNote(String[] fields) {
		this(Integer.parseInt(fields[1]), Integer.parseInt(fields[2]), Integer.parseInt(fields[3]), Integer.parseInt(fields[4]));
	}

	/** Whether the note is hit at the current point in the song
	 * 
	 * @param currentTime The current time of the song
	 * @param tapWindow The time period at which the note can be tapped
	 * @return Whether the note was hit */
	public boolean isHit(int currentTime, int tapWindow) {
		return (mStartTime >= currentTime - tapWindow && mStartTime <= currentTime + tapWindow);
	}

	public void update(DataPlayer player, float songSpeed) {
		mTopY = UtilsScreenSize.scaleY(((Utils.getCurrentSong().mMusicManager.getPlayPosition() - getStartTime()) * songSpeed) + DataPlayer.mUnscaledTapBoxY);
		mBottomY = mTopY + mNotePixelHeight;
		mLeft = player.getBoundingBoxLeft(mPosition) + (UtilsScreenSize.scaleX(DataPlayer.getUnscaledTapBoxWidth() - mNotePixelHeight) >> 1);
		
		if (isHoldNote())
			mHoldLine.update(songSpeed, Utils.getCurrentSong().mMusicManager.getPlayPosition());
	}

	@Override
	public void render(Canvas canvas) {

		
		// If hold note draw the hold line
		if (isHoldNote())
			mHoldLine.render(canvas);
			

		canvas.drawBitmap(getNoteBitmap(), mLeft, mTopY, null);
	}

	/** Gets the bitmap used to draw the note */
	private Bitmap getNoteBitmap() {
		if (isStarNote())
			return isHeld() && isHoldNote() ? ColourSchemeAssets.getNoteHeld(mPosition) : ColourSchemeAssets.getNoteStar(mPosition);
		return isHeld() && isHoldNote() ? ColourSchemeAssets.getNoteHeld(mPosition) : ColourSchemeAssets.getNote(mPosition);
	}

	@Override
	public int compareTo(DataNote note) {
		return mStartTime - note.mStartTime;
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

	public int getScaledXPosition() {
		return mLeft;
	}
	
	public int getScaledYPosition() {
		return mTopY;
	}
	
	public static void setNotePixelHeight(int height) {
		mNotePixelHeight = height;
	}

	public static int getNotePixelHeight() {
		if (mNotePixelHeight == -1)
			throw new ValueNotSetException("getNotePixelHeight()");
		else
			return mNotePixelHeight;
	}

	@Override
	public void dispose() {
		mHoldLine = null;
	}
}
