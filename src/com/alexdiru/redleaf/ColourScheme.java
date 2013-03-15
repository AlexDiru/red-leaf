package com.alexdiru.redleaf;

/** Represents a colour scheme for the song Different assets will be loaded for the graphics
 * depending on the theme chosen
 * @author Alex */
public class ColourScheme {

	/** The themes the song can have */
	public enum ThemeType {
		DISCOVERY, MONSOON, MAYDIE
	}

	/** The bitmaps for the tapbox */
	public String[] mTap = new String[4];

	/** The bitmaps for star notes */
	public String[] mNoteStar = new String[4];

	/** The bitmaps for when a tapbox is held */
	public String[] mTapHold = new String[4];

	/** Bitmaps used when a note is unheld/tap note */
	public String[] mNote = new String[4];

	/** Bitmaps used when a note is held */
	public String[] mNoteHeld = new String[4];

	/** Bitmaps used for a hold note trail when the note is held down */
	public String[] mNoteStreamHeld = new String[4];

	/** Bitmaps used for a hold note trail when the note is not held down */
	public String[] mNoteStreamUnheld = new String[4];

	/** Bitmap used for the background */
	public String mBackground;

	/** The alpha level of the background */
	public int mBackgroundAlpha;

	public String mStarPower;

	/** Initialises all the file paths with the theme given
	 * @param themeType The theme to base the files on */
	public ColourScheme(ThemeType themeType) {

		switch (themeType) {
		case DISCOVERY:
			mBackground = "discovery.png";
			mBackgroundAlpha = 213;

			mTap[0] = "L.png";
			mTap[1] = "O.png";
			mTap[2] = "S.png";
			mTap[3] = "T.png";

			mTapHold[0] = "Li.png";
			mTapHold[1] = "Oi.png";
			mTapHold[2] = "Si.png";
			mTapHold[3] = "Ti.png";

			mNote[0] = "redleft.png";
			mNote[1] = "reddown.png";
			mNote[2] = "redup.png";
			mNote[3] = "redright.png";

			mNoteStar[0] = "yellowleft.png";
			mNoteStar[1] = "yellowdown.png";
			mNoteStar[2] = "yellowup.png";
			mNoteStar[3] = "yellowright.png";

			mNoteHeld[0] = "blueleft.png";
			mNoteHeld[1] = "bluedown.png";
			mNoteHeld[2] = "blueup.png";
			mNoteHeld[3] = "blueright.png";

			mNoteStreamHeld[0] = "bluedowntrail.png";
			mNoteStreamHeld[1] = "bluedowntrail.png";
			mNoteStreamHeld[2] = "bluedowntrail.png";
			mNoteStreamHeld[3] = "bluedowntrail.png";

			mNoteStreamUnheld[0] = "reddowntrail.png";
			mNoteStreamUnheld[1] = "reddowntrail.png";
			mNoteStreamUnheld[2] = "reddowntrail.png";
			mNoteStreamUnheld[3] = "reddowntrail.png";

			mStarPower = "silverflame.png";

			break;
		case MONSOON:
			mBackground = "monsoon.png";

			mTap[0] = "Li.png";
			mTap[1] = "Oi.png";
			mTap[2] = "Si.png";
			mTap[3] = "Ti.png";

			mTapHold[0] = "L.png";
			mTapHold[1] = "O.png";
			mTapHold[2] = "S.png";
			mTapHold[3] = "T.png";

			mNote[0] = "note.png";
			mNote[1] = "note.png";
			mNote[2] = "note.png";
			mNote[3] = "note.png";
			break;
		case MAYDIE:
			mBackground = "maydie.png";

			mTap[0] = "L.png";
			mTap[1] = "O.png";
			mTap[2] = "S.png";
			mTap[3] = "T.png";

			mTapHold[0] = "Li.png";
			mTapHold[1] = "Oi.png";
			mTapHold[2] = "Si.png";
			mTapHold[3] = "Ti.png";

			mNote[0] = "note3.png";
			mNote[1] = "note3.png";
			mNote[2] = "note3.png";
			mNote[3] = "note3.png";
			break;
		}
	}
}
