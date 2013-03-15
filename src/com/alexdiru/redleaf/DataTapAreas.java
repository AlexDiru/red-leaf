package com.alexdiru.redleaf;

import android.graphics.Canvas;

import com.alexdiru.redleaf.ColourScheme.ThemeType;

public class DataTapAreas {

	private static final int TAP_AREAS = 4;
	private static final int TAP_AREA_HEIGHT = 160;
	public static final int TAP_AREA_WIDTH = 120;
	private static final int TAP_AREA_GAP = (720 - TAP_AREA_WIDTH * 4)/5;

	private DataBoundingBox[] mTapBoundingBoxes;
	private DataSong mSong;

	/** Handles the rendering according to the colour scheme */
	private GUIRenderer mGUIRenderer = new GUIRenderer(new ColourScheme(ThemeType.DISCOVERY), TAP_AREA_WIDTH, TAP_AREA_HEIGHT);

	/** Handles the player's touches */
	private DataTouchMap mTouchMap = new DataTouchMap();

	// Gameplay
	private int mStreak;
	private int mMultiplier = 1;
	private int mTappedCount;
	private int mMisses;
	private int mScore;

	/** Y coordinate of the top of the tapboxes */
	public int mTapBoxTop;

	/** Y coordinate of the bottom of the tapboxes */
	public int mTapBoxBottom;
	
	/** The tap window in ms */
	private int mTapWindow = 200;
	
	private int mStarNoteStreak = 0;
	private boolean mStarPowerActive = false;

	public DataTapAreas(DataSong song) {
		mSong = song;
		initialiseBackgroundAndTapBoxes();
	}

	private void initialiseBackgroundAndTapBoxes() {
		//Create the bounding boxes
		mTapBoundingBoxes = new DataBoundingBox[TAP_AREAS];
		for (int t = 0; t < TAP_AREAS; t++)
			mTapBoundingBoxes[t] = new DataBoundingBox();
		
		//Get the tapbox height boundaries
		mTapBoxTop = UtilsScreenSize.scaleY(GameView.TAPCIRCLES_Y);
		mTapBoxBottom = mTapBoxTop + UtilsScreenSize.scaleY(TAP_AREA_HEIGHT);
		
		//Update the tapboxes according to their size
		mTapBoundingBoxes[0].update(UtilsScreenSize.scaleX(TAP_AREA_GAP), mTapBoxTop, UtilsScreenSize.scaleX(TAP_AREA_GAP + TAP_AREA_WIDTH), mTapBoxBottom);
		mTapBoundingBoxes[1].update(UtilsScreenSize.scaleX(TAP_AREA_GAP * 2 + TAP_AREA_WIDTH), mTapBoxTop, UtilsScreenSize.scaleX(TAP_AREA_GAP * 2 + TAP_AREA_WIDTH * 2), mTapBoxBottom);
		mTapBoundingBoxes[2].update(UtilsScreenSize.scaleX(TAP_AREA_GAP * 3 + TAP_AREA_WIDTH * 2), mTapBoxTop, UtilsScreenSize.scaleX(TAP_AREA_GAP * 3 + TAP_AREA_WIDTH * 3), mTapBoxBottom);
		mTapBoundingBoxes[3].update(UtilsScreenSize.scaleX(TAP_AREA_GAP * 4 + TAP_AREA_WIDTH * 3), mTapBoxTop, UtilsScreenSize.scaleX(TAP_AREA_GAP * 4 + TAP_AREA_WIDTH * 4), mTapBoxBottom);

		//Render the tapboxes on the same bitmap as the background
		mGUIRenderer.setupBackgroundWithTapboxes(mTapBoundingBoxes);
	}

	public void successfulTap(DataNote note) {
		mStreak++;
		mTappedCount++;
		

		switch (mStreak) {
		case 20:
			mMultiplier = 2;
			break;
		case 30:
			mMultiplier = 3;
			break;
		case 40:
			mMultiplier = 4;
			break;
		case 50:
			mMultiplier = 8;
			break;
		}
		
		//Check star
		if (note.isStarNote())
			mStarNoteStreak++;
		
		if (mStarNoteStreak == 4) 
			mStarPowerActive = true;
		
		if (mStarPowerActive)
			mScore += 1600;
		else
			mScore += 100 * mMultiplier;
	}

	public void unsuccessfulTap() {
		mStreak = 0;
		mStarNoteStreak = 0;
		mScore -= 30;
		mMultiplier = 1;
	}

	/** Called when the player places a new finger on the screen, check if a tapbox is touched and if
	 * so attempts to tap a note
	 * @param x The x coordinate of the touch
	 * @param y The y coordinate of the touch
	 * @param pid The index of the touch */
	public void handleTouchDown(int x, int y, int pid, int currentTime) {
		for (int i = 0; i < TAP_AREAS; i++)
			if (mTapBoundingBoxes[i].isTouched(x, y, TAP_AREA_GAP/2, TAP_AREA_GAP*2)) {
				mSong.tap(i, currentTime);
				mTouchMap.put(pid, i);
				break;
			}
	}

	/** Triggered when the player doesn't tap a note and it goes below the tapbox area */
	public void miss() {
		unsuccessfulTap();
		mMisses++;
	}

	/** Called when the player lifts the secondary finger from the screen
	 * @param pid The index of the finger (secondary finger so it will be 1) */
	public void handleTouchUp(int pid) {
		Integer position = mTouchMap.get(pid);
		if (mSong != null && position != null)
			mSong.unhold(position);
		
		mTouchMap.remove(pid);
	}

	/** Called when the player lifts the primary finger from the screen - no touches will be left on
	 * the screen */
	public void handleAllTouchesUp() {
		mSong.unholdAll();
		mTouchMap.clear();
	}

	/** Draws this object, this includes the background and the tapboxes
	 * @param canvas The canvas to draw to */
	public void draw(Canvas canvas) {
		mGUIRenderer.drawBackground(canvas);

		for (int t = 0; t < TAP_AREAS; t++)
			if (mTouchMap.isTouched(t))
				mGUIRenderer.drawTapBox(canvas, mTapBoundingBoxes[t], t, true);
	}

	public GUIRenderer getRenderer() {
		return mGUIRenderer;
	}

	public int getBoundingBoxTop() {
		return mTapBoundingBoxes[0].getTop();
	}

	public int getBoundingBoxBottom() {
		return mTapBoundingBoxes[0].getBottom();
	}

	public int getBoundingBoxLeft(int position) {
		return mTapBoundingBoxes[position].getLeft();
	}

	public int getTappedCount() {
		return mTappedCount;
	}

	public int getMissedCount() {
		return mMisses;
	}

	public int getStreak() {
		return mStreak;
	}

	public int getMultiplier() {
		return mMultiplier;
	}
	
	public int getScore() {
		return mScore;
	}
	
	public int getTapWindow() {
		return mTapWindow;
	}

	public void increaseScore(int score) {
		mScore += score;
	}
	
	public boolean isStarPowerActive() {
		return mStarPowerActive;
	}
}