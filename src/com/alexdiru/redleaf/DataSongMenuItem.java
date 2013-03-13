package com.alexdiru.redleaf;

/** Represents a song on the song picker, this stores all the information required
 * @author Alex */
public class DataSongMenuItem {

	/** The name of the song */
	public String mSongName;

	/** The album the song belongs to */
	private String mAlbumName;

	/** The file which contains the notes/other data for the song */
	public String mNoteFile;

	public DataSongMenuItem(String songName, String albumName, String noteFile) {
		mSongName = songName;
		mAlbumName = albumName;
		mNoteFile = noteFile;
	}

	/** Converts the menu item to a string displayed to the user */
	public String toString() {
		return mSongName + " (" + mAlbumName + ")";
	}

}
