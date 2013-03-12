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
	private int mMultiplier;
	private int mTappedCount;
	private int mMisses;
	private int mScore;

	/** Y coordinate of the top of the tapboxes */
	public int mTapBoxTop;

	/** Y coordinate of the bottom of the tapboxes */
	public int mTapBoxBottom;

	public DataTapAreas(DataSong song) {

		mTapBoundingBoxes = new DataBoundingBox[TAP_AREAS];

		for (int t = 0; t < TAP_AREAS; t++)
			mTapBoundingBoxes[t] = new DataBoundingBox();

		mSong = song;
		
		initialiseTapBoxes();
	}

	private void initialiseTapBoxes() {
		int y1 = UtilsScreenSize.scaleY(GameView.TAPCIRCLES_Y);
		int y2 = y1 + UtilsScreenSize.scaleY(TAP_AREA_HEIGHT);
		
		mTapBoxTop = y1;
		mTapBoxBottom = y2;
		
		int a = 720 - TAP_AREA_WIDTH * 4;
		int b = a / 5;
		
		mTapBoundingBoxes[0].update(UtilsScreenSize.scaleX(b), y1, UtilsScreenSize.scaleX(b + TAP_AREA_WIDTH), y2);
		mTapBoundingBoxes[1].update(UtilsScreenSize.scaleX(b * 2 + TAP_AREA_WIDTH), y1, UtilsScreenSize.scaleX(b * 2 + TAP_AREA_WIDTH * 2), y2);
		mTapBoundingBoxes[2].update(UtilsScreenSize.scaleX(b * 3 + TAP_AREA_WIDTH * 2), y1, UtilsScreenSize.scaleX(b * 3 + TAP_AREA_WIDTH * 3), y2);
		mTapBoundingBoxes[3].update(UtilsScreenSize.scaleX(b * 4 + TAP_AREA_WIDTH * 3), y1, UtilsScreenSize.scaleX(b * 4 + TAP_AREA_WIDTH * 4), y2);
	}

	public void successfulTap() {
		mStreak++;
		mTappedCount++;
		
		mScore += 100 * mMultiplier;

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
	}

	public void unsuccessfulTap() {
		mStreak = 0;
		mMultiplier = 1;
		mScore -= 30;
	}

	/*private void handleTouchHold(int x, int y, int pid) {
		if (mTouchMap.get(pid) == null)
			return;

		if (!mTapBoundingBoxes[mTouchMap.get(pid)].isTouched(x, y)) {
			mTouchMap.remove(pid);
		}
	}*/

	/** Called when the player places a new finger on the screen, check if a tapbox is touched and if
	 * so attempts to tap a note
	 * @param x The x coordinate of the touch
	 * @param y The y coordinate of the touch
	 * @param pid The index of the touch */
	public void handleTouchDown(int x, int y, int pid) {
		for (int i = 0; i < TAP_AREAS; i++)
			if (mTapBoundingBoxes[i].isTouched(x, y, TAP_AREA_GAP/2)) {
				mSong.tap(i);
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
			mGUIRenderer.drawTapBox(canvas, mTapBoundingBoxes[t], t, mTouchMap.isTouched(t));
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

	public void increaseScore(int score) {
		mScore += score;
	}
}
