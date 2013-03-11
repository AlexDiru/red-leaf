package com.alexdiru.redleaf;

public class DataSongMenuItem {

	public String mSongName;
	private String mAlbumName;
	public String mNoteFile;
	
	public DataSongMenuItem(String songName, String albumName, String noteFile) {
		mSongName = songName;
		mAlbumName = albumName;
		mNoteFile = noteFile;
	}
	
	public String toString() {
		return mSongName + " (" + mAlbumName + ")";
	}

}
