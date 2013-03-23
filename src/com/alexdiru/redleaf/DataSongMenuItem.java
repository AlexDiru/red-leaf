package com.alexdiru.redleaf;

import java.util.Arrays;

/** Represents a song on the song picker, this stores all the information required
 * @author Alex */
public class DataSongMenuItem {

	/** The name of the song */
	public String mSongName;

	/** The album the song belongs to */
	private String mAlbumName;

	/** The file which contains the notes/other data for the song */
	public String mNoteFile;
	
	/** Whether the song has notes for certain difficulties, i.e. if mDA[2] is true, then hard mode is available */
	private boolean[] mDifficultiesAvailable = new boolean[3];

	public DataSongMenuItem(String songName, String albumName, String noteFile, String difficulties) {
		mSongName = songName;
		mAlbumName = albumName;
		mNoteFile = noteFile;
		
		Arrays.fill(mDifficultiesAvailable, false);
		String[] difCsv = difficulties.split("\\,");
		
		for (int i = 0; i < difCsv.length; i++)
			try {
				mDifficultiesAvailable[Integer.parseInt(difCsv[i])] = true;
			} catch (Exception e) {
			}
	}

	/** Converts the menu item to a string displayed to the user */
	public String toString() {
		return mSongName + " (" + mAlbumName + ")";
	}

	public boolean hasEasyDifficulty() {
		return mDifficultiesAvailable[0];
	}
	public boolean hasMediumDifficulty() {
		return mDifficultiesAvailable[1];
	}
	public boolean hasHardDifficulty() {
		return mDifficultiesAvailable[2];
	}
}
