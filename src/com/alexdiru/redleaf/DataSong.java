package com.alexdiru.redleaf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Rect;

//http://code.google.com/p/beats2/source/browse/trunk/beats/src/com/beatsportable/beats/DataFile.java
public class DataSong {

	/** The pixel width/height of each note */
	public static final int NOTESIZE = DataTapAreas.TAP_AREA_WIDTH;
	
	/** The maximum number of notes that can be rendered */
	private static final int MAX_NOTES_ON_SCREEN = 30;

	/** Represents the levels of difficulty of the song */
	public enum DataSongDifficulty {
		EASY, MEDIUM, HARD
	}

	/** Artist of the song */
	public String mArtistName = "LOST";

	/** Album the song belongs to */
	public String mAlbumName = "DISCOVERY";

	/** Name of the song being played */
	public String mSongName = "";

	/** Current difficulty of the song */
	public DataSongDifficulty mDifficulty;

	/** Points to the file the music for the song is stored in */
	public String mMusicFile;

	/** Stores the notes of the songs, must be kept sorted according to the start time */
	public ArrayList<DataNote> mNotes;

	/** Manages the playing track for the song */
	public MusicManager mMusicManager;

	/** Manages the gameplay for the user */
	private DataTapAreas mTapAreas;

	/** Index of the next note to check to be rendered */
	private int mNoteIndex = 0;

	// Current notes being rendered
	public ArrayList<DataNote> mRenderNotes = new ArrayList<DataNote>(MAX_NOTES_ON_SCREEN);
	
	/** The notes which are held by the player */
	private DataNote[] mHeldNotes = new DataNote[4];

	public DataSong() {
		Arrays.fill(mHeldNotes, null);
	}

	public DataSong(String filePath) {
		this();
		
		// Parse the file
		parse(filePath, 0);

	}

	public void generateRandomNotes(int difficulty) {
		mNotes.clear();

		int maxNoteSpace;
		int minNoteSpace;
		int doubleNoteChance;

		switch (difficulty) {
		case 0:
			minNoteSpace = 250;
			maxNoteSpace = 500;
			doubleNoteChance = 5;
			break;
		case 1:
			minNoteSpace = 200;
			maxNoteSpace = 300;
			doubleNoteChance = 15;
			break;
		default:
		case 2:
			minNoteSpace = 150;
			maxNoteSpace = 250;
			doubleNoteChance = 20;
			break;
		case 3:
			// STRESS TEST
			minNoteSpace = 10;
			maxNoteSpace = 20;
			doubleNoteChance = 50;
			break;
		}
		Random random = new Random();
		int position = 0;
		while (position < mMusicManager.getLength()) {
			int space = random.nextInt(maxNoteSpace - minNoteSpace) + minNoteSpace;
			position += space;

			int a = random.nextInt(4);
			if (random.nextInt(100) < doubleNoteChance) {
				int b;
				do {
					b = random.nextInt(4);
				} while (b != a);
				mNotes.add(new DataNote(position, 0, 0, b));
			}
			mNotes.add(new DataNote(position, 0, 0, a));
		}
	}

	/** Parse a data file to extract the song data of the specified difficulty
	 * 
	 * @param filePath
	 * @param currentDifficulty */
	private void parse(String filePath, int currentDifficulty) {
		// Read all the lines from the file
		ArrayList<String> lines = UtilsFileIO.readAllLines(filePath);
		boolean parseNotes = false;

		mNotes = new ArrayList<DataNote>();
		mDifficulty = DataSongDifficulty.values()[currentDifficulty];

		for (String line : lines) {
			try {
				String[] fields = UtilsArray.trimAll(line.split("\\ "));

				if (fields[0].equals("artist"))
					mArtistName = UtilsArray.concatArray(fields, " ", 1);
				else if (fields[0].equals("album"))
					mAlbumName = UtilsArray.concatArray(fields, " ", 1);
				else if (fields[0].equals("song"))
					mSongName = UtilsArray.concatArray(fields, " ", 1);
				else if (fields[0].equals("mp3"))
					mMusicFile = UtilsArray.concatArray(fields, " ", 1);
				/* else if (fields[0].equals("background")) mBackgroundFile =
				 * UtilsArray.concatArray(fields, " ", 1); */
				else if (fields[0].equals("begindifficulty") && fields[1].equals(String.valueOf(currentDifficulty)))
					parseNotes = true;
				else if (parseNotes && fields[0].equals("note"))
					mNotes.add(new DataNote(fields));
				else if (fields[0].equals("enddifficulty"))
					parseNotes = false;

			} catch (Exception ex) {
			}
		}
	}
	
	public void updateNotes(int currentTime, int renderDistance, int tapCirclesHeight) {

		// Check the render list to remove any tapped or off screen notes
		// Note: must use iterator to delete otherwise MASSIVE speed drop (very noticable jitter)
		DataNote current = null;
		for (int i = 0; i < mRenderNotes.size(); i++) {

			current = mRenderNotes.get(i);

			// (Applies to tap notes) If the note has been fallen off the screen remove it from the render list
			boolean tapFallen = current.mTopY > mTapAreas.mTapBoxBottom && current.isTapNote() && !current.mTapped;

			// (Applies to hold notes) If the note hasn't been tapped and is below the tapbox
			boolean heldInitialFallen = (current.mTopY  > mTapAreas.mTapBoxBottom) && current.isHoldNote() && !current.mTapped;
			
			// (Applies to hold notes) If the note has been held and the line has fallen off screen
			boolean heldFallen = current.mTapped && !current.mBeingHeld && current.isHoldNote();
			
			// If a regular note has been tapped or fallen off the screen
			if (current.mTapped && current.isTapNote() || tapFallen || heldInitialFallen || heldFallen) {
				mRenderNotes.remove(i);
				i--;

				if (tapFallen || heldInitialFallen) 
					mTapAreas.miss();
				

				continue;
			}
			// Else no chance of any other notes being tapped
			else if (current.mBottomY > mTapAreas.mTapBoxTop && current.isTapNote())
				break;
		}

		// Too many notes on screen, no need to add more
		if (mRenderNotes.size() >= MAX_NOTES_ON_SCREEN)
			return;

		// First index will be the index of the note we haven't yet rendered
		// This will be the note which is due to next fall onto the screen
		int startIndex = mNoteIndex;

		// Limit the amount of notes which can be displayed on screen
		// In the case of a stress test being done with ~29k notes
		// StartIndex -> mNotes.size() will be a lot of notes
		// Therefore only lookahead a certain amount
		int endIndex = startIndex + MAX_NOTES_ON_SCREEN;
		if (endIndex > mNotes.size())
			endIndex = mNotes.size();

		DataNote note = null;
		for (int i = startIndex; i < endIndex; i++) {
			note = mNotes.get(i);
			startIndex++;
			if (note.mStartTime - currentTime < renderDistance) {
				mRenderNotes.add(note);
				mNoteIndex = startIndex;
			}
			else
				break;

			if (mRenderNotes.size() >= MAX_NOTES_ON_SCREEN)
				break;
		}
	}

	/** Renders the notes of the song
	 * @param canvas The canvas to render to
	 * @param tapAreas */
	public void renderNotes(Canvas canvas, DataTapAreas tapAreas) {

		// Assign the notes to a local array list
		ArrayList<DataNote> notes = Utils.getCurrentSong().mRenderNotes;

		// Iterate through the notes to render
		for (int i = notes.size() - 1; i >= 0; i--) {

			DataNote note = notes.get(i);

			if (note == null)
				continue;

			// Update note coordinates
			note.mTopY = Utils.getCurrentSong().mMusicManager.getPlayPosition() - note.mStartTime + GameView.TAPCIRCLES_Y;
			note.mBottomY = note.mTopY + NOTESIZE;
			int noteXPosition = tapAreas.getBoundingBoxLeft(note.mPosition) + ((DataTapAreas.TAP_AREA_WIDTH - NOTESIZE) >> 1);

			canvas.drawBitmap(note.mBeingHeld && note.isHoldNote() ? tapAreas.getRenderer().getNoteHeld(note.mPosition) : tapAreas.getRenderer().getNote(note.mPosition), noteXPosition, note.mTopY, null);

			if (note.isHoldNote())
				note.drawHoldLine(canvas, tapAreas.getRenderer().getHoldLineHeldPaint(note.mPosition) , tapAreas.getRenderer().getHoldLineUnheldPaint(note.mPosition) , noteXPosition);
		}
	}

	private DataNote getNextTappableNoteInPosition(int position) {
		DataNote note = null;
		for (int i = 0; i < mRenderNotes.size(); i++) {
			note = mRenderNotes.get(i);
			if (!note.mTapped)
				if (note.isHit(mTapAreas.getBoundingBoxTop(), mTapAreas.getBoundingBoxBottom()))
					if (note.mPosition == position)
						return note;
		}
		return null;
	}

	private boolean isTapSuccessful(DataNote note, int position) {
		if (note == null)
			return false;

		return note.isHit(mTapAreas.getBoundingBoxTop(), mTapAreas.getBoundingBoxBottom());
	}

	public boolean tap(int position) {
		DataNote note = getNextTappableNoteInPosition(position);

		if (isTapSuccessful(note, position)) {
		
			note.mTapped = true;
			
			if (note.isHoldNote()) {
				note.mBeingHeld = true;
				mHeldNotes[position] = note;
			}
			
			mTapAreas.successfulTap();
		} else
			mTapAreas.unsuccessfulTap();

		return false;
	}
	
	/** Called when the player lifts a finger up, makes sure any held notes are unheld */
	public void unhold(int position) {
		if (mHeldNotes[position] != null) {
			mHeldNotes[position].mBeingHeld = false;
			mHeldNotes[position] = null;
		}
	}
	
	public void unholdAll() {
		for (int i = 0; i < mHeldNotes.length; i++)
			unhold(i);
	}

	public void hold(int position) {
	}

	public void setTapAreas(DataTapAreas tapAreas) {
		mTapAreas = tapAreas;
	}

}
