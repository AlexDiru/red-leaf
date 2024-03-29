package com.alexdiru.redleaf;

import android.graphics.Canvas;

import com.alexdiru.redleaf.ColourScheme.ThemeType;
import com.alexdiru.redleaf.exception.SongNotOverException;
import com.alexdiru.redleaf.exception.ValueNotSetException;
import com.alexdiru.redleaf.interfaces.IDisposable;
import com.alexdiru.redleaf.interfaces.IRenderable;

public class DataPlayer implements IRenderable, IDisposable {

	/** Unscaled Y position of the tapboxes */
	public static final int mUnscaledTapBoxY = (int) (1280 / 1.3061);

	/** The number of star notes required in a streak for star power */
	private static final int mNoteStreakRequiredForStarPower = 10;

	/** The number of milliseconds star power lasts for */
	private static final int mStarPowerDuration = 10000;

	/** The pixel height of the tapboxes when ScaleY = 1 */
	private static final int mUnscaledTapBoxHeight = 160;

	/** Set in ColourSchemeAssets as is dependent on the ratio of the images
	 * loaded */
	private static int mUnscaledTapBoxWidth = -1;

	/** Set in ColourSchemeAssets as is dependent on the ratio of the images
	 * loaded */
	private static int mUnscaledTapBoxGap = -1;

	/** The tapboxes the player touches to tap/hold notes */
	private DataTapBox[] mTapBoxes;

	/** The song the player is playing */
	private DataSong mSong;

	/** Handles the player's touches */
	private DataTouchMap mTouchMap = new DataTouchMap();
	
	/** Handles the star power */
	private DataStarPower mStarPower;

	// Gameplay
	private int mStreak;
	private int mMultiplier = 1;
	private int mTappedCount;
	private int mMisses;
	private int mMistaps;
	private int mScore;

	/** Y coordinate of the top of the tapboxes */
	public int mTapBoxTop;

	/** Y coordinate of the bottom of the tapboxes */
	public int mTapBoxBottom;

	/** The tap window in ms */
	private int mTapWindow = 200;

	public DataPlayer() {
		ColourSchemeAssets.initialise(new ColourScheme(ThemeType.DISCOVERY), mUnscaledTapBoxHeight);
		
		mSong = Utils.getCurrentSong();
		mSong.setPlayer(this);

		initialiseBackgroundAndTapBoxes();

		mStarPower = new DataStarPower();

	}

	/** Gets the world height of the tapbox
	 * 
	 * @return */
	public static int getUnscaledTapBoxHeight() {
		return mUnscaledTapBoxHeight;
	}

	/** Sets the world width of the tapbox
	 * 
	 * @param width */
	public static void setUnscaledTapBoxWidth(int width) {
		mUnscaledTapBoxWidth = width;
	}

	/** Gets the world width of the tapbox
	 * 
	 * @return */
	public static int getUnscaledTapBoxWidth() {
		if (mUnscaledTapBoxWidth == -1)
			throw new ValueNotSetException("getScaledTapBoxWidth()");
		return mUnscaledTapBoxWidth;
	}

	/** Sets the world width of the gaps between the tapboxes
	 * 
	 * @param gap */
	public static void setUnscaledTapBoxGap(int gap) {
		mUnscaledTapBoxGap = gap;
	}

	/** Gets the world width of the gaps between the tapboxes
	 * 
	 * @return */
	public static int getUnscaledTapBoxGap() {
		if (mUnscaledTapBoxGap == -1)
			throw new ValueNotSetException("getScaledTapBoxGap()");
		return mUnscaledTapBoxGap;
	}
	
	private void initialiseBackgroundAndTapBoxes() {
		// Create the bounding boxes
		mTapBoxes = new DataTapBox[4];
		for (int t = 0; t < 4; t++) {
			mTapBoxes[t] = new DataTapBox();
			mTapBoxes[t].setRectangleWidth(Math.round(UtilsScreenSize.scaleY(14)));
			mTapBoxes[t].setUnheldBitmap(ColourSchemeAssets.getTapBox(t));
			mTapBoxes[t].setHeldBitmap(ColourSchemeAssets.getTapBoxHeld(t));
		}

		// Get the tapbox height boundaries
		mTapBoxTop = UtilsScreenSize.scaleY(DataPlayer.mUnscaledTapBoxY);
		mTapBoxBottom = mTapBoxTop + UtilsScreenSize.scaleY(mUnscaledTapBoxHeight);

		// Update the tapboxes according to their size
		mTapBoxes[0].update(UtilsScreenSize.scaleX(getUnscaledTapBoxGap()), mTapBoxTop);
		mTapBoxes[1].update(UtilsScreenSize.scaleX(getUnscaledTapBoxGap() * 2) + mUnscaledTapBoxWidth, mTapBoxTop);
		mTapBoxes[2].update(UtilsScreenSize.scaleX(getUnscaledTapBoxGap() * 3) + mUnscaledTapBoxWidth * 2, mTapBoxTop);
		mTapBoxes[3].update(UtilsScreenSize.scaleX(getUnscaledTapBoxGap() * 4) + mUnscaledTapBoxWidth * 3, mTapBoxTop);

		// Render the tapboxes on the same bitmap as the background
		ColourSchemeAssets.setupBackgroundsWithTapboxes(mTapBoxes);
	}

	/** Called when a successful tap is made on a note, updates all of the
	 * player's streaks and multipliers
	 * 
	 * @param note The note tapped */
	public void successfulTap(DataNote note) {
		mTappedCount++;
		updateStreak(note.isStarNote());
		updateMultiplier();
		updateScore();
	}

	/** Updates the player's score with respect to the multiplier */
	private void updateScore() {
		if (mStarPower.isActive())
			mScore += 1600;
		else
			mScore += 100 * mMultiplier;
	}

	/** Updates the player's streaks */
	private void updateStreak(boolean isStarNote) {
		// Regular streak
		mStreak++;

		// Star note streak
		if (isStarNote)
			mStarPower.increaseStreak();

		// Star note activation
		if (mStarPower.getStreak() == mNoteStreakRequiredForStarPower) {
			mStarPower.increaseNumberAvailable();
			mStarPower.resetStreak();
		}
	}

	/** Updates the multiplier according to the streak the player has */
	private void updateMultiplier() {
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

	/** Called when a player taps at the wrong time and doesn't hit a note */
	public void mistap() {
		mMistaps++;
		unsuccessfulTap();
	}

	/** Provides common functionality for when either a note is missed or a
	 * mistap is made, resets all of the player's streaks and multipliers */
	private void unsuccessfulTap() {
		mStreak = 0;
		mStarPower.resetStreak();
		mScore -= 30;
		mMultiplier = 1;
	}

	/** Called when the player places a new finger on the screen, check if a
	 * tapbox is touched and if so attempts to tap a note
	 * 
	 * @param x The x coordinate of the touch
	 * @param y The y coordinate of the touch
	 * @param pid The index of the touch */
	public void handleTouchDown(int x, int y, int pid, int currentTime) {

		// Check the tapboxes being touched
		for (int i = 0; i < 4; i++)
			if (mTapBoxes[i].isTouched(x, y, UtilsScreenSize.scaleX(getUnscaledTapBoxGap() >> 1), UtilsScreenSize.scaleX(getUnscaledTapBoxGap() << 1))) {
				mTapBoxes[i].hold();
				mSong.tap(i, currentTime,mTapWindow);
				mTouchMap.put(pid, i);
				return;
			}

		// Check star power being touched
		mStarPower.handleTouch(x,y, currentTime);

	}

	/** Triggered when the player doesn't tap a note and it goes below the tapbox
	 * area */
	public void miss() {
		unsuccessfulTap();
		mMisses++;
	}

	/** Called when the player lifts the secondary finger from the screen
	 * 
	 * @param pid The index of the finger (secondary finger so it will be 1) */
	public void handleTouchUp(int pid) {
		Integer position = mTouchMap.get(pid);
		if (mSong != null && position != null) {
			mSong.unhold(position);
			mTapBoxes[position].unhold();
		}

		mTouchMap.remove(pid);
	}

	/** Called when the player lifts the primary finger from the screen - no
	 * touches will be left on the screen */
	public void handleAllTouchesUp() {
		mSong.unholdAll();
		mTouchMap.clear();
	}

	/** Draws this object, this includes the background and the tapboxes
	 * 
	 * @param canvas The canvas to draw to */
	@Override
	public void render(Canvas canvas) {
		canvas.drawBitmap(ColourSchemeAssets.getBackground(mStarPower.isActive()), 0, 0, null);

		for (int t = 0; t < 4; t++)
			if (mTouchMap.isTouched(t))
				mTapBoxes[t].render(canvas);

		mStarPower.render(canvas);
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
	
	@Override
	public void dispose() {
		UtilsDispose.disposeAll(mTapBoxes);
		(new ColourSchemeAssets()).dispose();
		UtilsDispose.dispose(mTouchMap);
		mTouchMap = null;
		UtilsDispose.dispose(mStarPower);
		mStarPower = null;
		UtilsDispose.dispose(mSong);
		mSong = null;
	}

	public boolean isStarPowerActive() {
		return mStarPower.isActive();
	}

	public void update(int currentTime) {
		mStarPower.update(currentTime);
	}
}