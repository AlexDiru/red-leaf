package com.alexdiru.redleaf;

import android.graphics.Canvas;

import com.alexdiru.redleaf.ColourScheme.ThemeType;
import com.alexdiru.redleaf.interfaces.IDisposable;
import com.alexdiru.redleaf.interfaces.IRenderable;

public class DataPlayer implements IRenderable, IDisposable {

	private static final int TAP_AREAS = 4;
	public static final int TAP_AREA_HEIGHT = 160;
	private static final int NOTE_STREAK_REQUIRED_FOR_STAR_POWER = 10;
	
	private static final int STAR_POWER_DURATION = 10000;


	public static int TAP_AREA_WIDTH;
	public static int TAP_AREA_GAP;
	
	private DataTapBox[] mTapBoxes;
	private DataSong mSong;

	/** Handles the rendering according to the colour scheme */
	private ColourSchemeAssets mColourSchemeAssets = new ColourSchemeAssets(new ColourScheme(ThemeType.DISCOVERY),TAP_AREA_HEIGHT);

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
	private int mStarPowersAvailable = 0;
	private boolean mStarPowerActive = false;
	private DataBoundingBox mStarPowerBoundingBox;
	private int mStarPowerTimeOfActivation;

	public DataPlayer() {
		mSong = Utils.getCurrentSong();
		mSong.setTapAreas(this);
		
		initialiseBackgroundAndTapBoxes();
		
		//Star power bounding box
		mStarPowerBoundingBox = new DataBoundingBox(mColourSchemeAssets.getStarPower());
		mStarPowerBoundingBox.update(UtilsScreenSize.getScreenWidth()/2 - mColourSchemeAssets.getStarPower().getWidth()/2,UtilsScreenSize.getScreenHeight()/2 , 
				UtilsScreenSize.getScreenWidth()/2 + mColourSchemeAssets.getStarPower().getWidth()/2,UtilsScreenSize.getScreenHeight()/2 + mColourSchemeAssets.getStarPower().getHeight());
	}
	
	public void update(int currentTime) {
		if (mStarPowerActive && mStarPowerTimeOfActivation + STAR_POWER_DURATION < currentTime)
			endStarPower();
	}
	
	private void startStarPower(int currentTime) {
		mStarPowerActive = true;
		mStarPowersAvailable--;
		mStarPowerTimeOfActivation = currentTime;
	}
	
	private void endStarPower() {
		mStarPowerActive = false;
	}

	private void initialiseBackgroundAndTapBoxes() {
		//Create the bounding boxes
		mTapBoxes = new DataTapBox[TAP_AREAS];
		for (int t = 0; t < TAP_AREAS; t++) {
			mTapBoxes[t] = new DataTapBox();
			mTapBoxes[t].setRectangleWidth(Math.round(UtilsScreenSize.scaleY(6)));
			mTapBoxes[t].setUnheldBitmap(mColourSchemeAssets.getTapBox(t));
			mTapBoxes[t].setHeldBitmap(mColourSchemeAssets.getTapBoxHeld(t));
		}
		
		//Get the tapbox height boundaries
		mTapBoxTop = UtilsScreenSize.scaleY(GameView.TAPCIRCLES_Y);
		mTapBoxBottom = mTapBoxTop + UtilsScreenSize.scaleY(TAP_AREA_HEIGHT);
		
		//Update the tapboxes according to their size
		mTapBoxes[0].update(UtilsScreenSize.scaleX(TAP_AREA_GAP), mTapBoxTop);//, UtilsScreenSize.scaleX(TAP_AREA_GAP + TAP_AREA_WIDTH), mTapBoxBottom);
		mTapBoxes[1].update(UtilsScreenSize.scaleX(TAP_AREA_GAP * 2) + TAP_AREA_WIDTH, mTapBoxTop);//, UtilsScreenSize.scaleX(TAP_AREA_GAP * 2 ) + TAP_AREA_WIDTH * 2, mTapBoxBottom);
		mTapBoxes[2].update(UtilsScreenSize.scaleX(TAP_AREA_GAP * 3)+ TAP_AREA_WIDTH * 2, mTapBoxTop);//, UtilsScreenSize.scaleX(TAP_AREA_GAP * 3) + TAP_AREA_WIDTH * 3, mTapBoxBottom);
		mTapBoxes[3].update(UtilsScreenSize.scaleX(TAP_AREA_GAP * 4)+ TAP_AREA_WIDTH * 3, mTapBoxTop);//, UtilsScreenSize.scaleX(TAP_AREA_GAP * 4) + TAP_AREA_WIDTH * 4, mTapBoxBottom);
		
		//Render the tapboxes on the same bitmap as the background
		mColourSchemeAssets.setupBackgroundsWithTapboxes(mTapBoxes);
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
		else 
			mStarNoteStreak = 0;
		
		if (mStarNoteStreak == NOTE_STREAK_REQUIRED_FOR_STAR_POWER) {
			mStarPowersAvailable++;
			mStarNoteStreak = 0;
		}
		
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
		
		//Check the tapboxes being touched
		for (int i = 0; i < TAP_AREAS; i++)
			if (mTapBoxes[i].isTouched(x, y, TAP_AREA_GAP/2, TAP_AREA_GAP*2)) {
				mTapBoxes[i].hold();
				mSong.tap(i, currentTime);
				mTouchMap.put(pid, i);
				return;
			}
		
		//Check star power being touched
		if (mStarPowersAvailable > 0)
			if (!mStarPowerActive)
				if (mStarPowerBoundingBox.isTouched(x, y)) 
					startStarPower(currentTime);
		
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
		if (mSong != null && position != null) {
			mSong.unhold(position);
			mTapBoxes[position].unhold();
		}
		
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
	@Override
	public void render(Canvas canvas) {
		canvas.drawBitmap(mColourSchemeAssets.getBackground(mStarPowerActive),0,0,null);

		for (int t = 0; t < TAP_AREAS; t++)
			if (mTouchMap.isTouched(t))
				mTapBoxes[t].render(canvas);
	
		if (mStarPowersAvailable > 0) { 
			mStarPowerBoundingBox.render(canvas);
		}
	}

	public ColourSchemeAssets getColourSchemeAssets() {
		return mColourSchemeAssets;
	}

	public int getBoundingBoxTop() {
		return mTapBoxes[0].getTop();
	}

	public int getBoundingBoxBottom() {
		return mTapBoxes[0].getBottom();
	}

	public int getBoundingBoxLeft(int position) {
		return mTapBoxes[position].getLeft();
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

	@Override
	public void dispose() {
		UtilsDispose.disposeAll(mTapBoxes);
		UtilsDispose.dispose(mColourSchemeAssets);
		UtilsDispose.dispose(mTouchMap);
		UtilsDispose.dispose(mStarPowerBoundingBox);
		UtilsDispose.dispose(mSong);
	}
}