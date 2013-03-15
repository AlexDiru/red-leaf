package com.alexdiru.redleaf.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.alexdiru.redleaf.DataSongMenuItem;
import com.alexdiru.redleaf.UtilsFileIO;

public class FileIOSongListParser {
	
	private ArrayList<DataSongMenuItem> mMenuItems;
	
	//Maps the toString() of the menu item to the menu item
	private Map<String, DataSongMenuItem> mMenuItemsHash;

	/**
	 * Creates a list of menu items based on the songs in the song list file
	 * @param songListFile
	 */
	public FileIOSongListParser(String songListFile) {
		//Read all lines from the file
		ArrayList<String> songLines = UtilsFileIO.readAllLines(songListFile);
		
		//Initialise the list of menu items and the hash
		mMenuItems = new ArrayList<DataSongMenuItem>();
		mMenuItemsHash = new HashMap<String, DataSongMenuItem>();
		
		for (String songLine : songLines) {
			try {
				//Split by pipe
				String[] fields = songLine.split("\\|");
				
				//Add each field to a DataSongMenuItem
				DataSongMenuItem item = new DataSongMenuItem(fields[0].trim(), fields[1].trim(), fields[2].trim());
				
				//Add item to the list and hash
				mMenuItems.add(item);
				mMenuItemsHash.put(item.toString(), item);
				
			} catch (Exception ex) {
				//Corrupted data
				continue;
			}
		}
	}
	
	/**
	 * Converts each menu item into readable text for each menu item
	 * @return
	 */
	public String[] getMenuItemsToDisplay() {
		String[] displayItems = new String[mMenuItems.size()];
		
		for (int m = 0; m < mMenuItems.size(); m++)
			displayItems[m] = mMenuItems.get(m).toString();
		
		return displayItems;	
	}
	
	/**
	 * Given the menu item string, returns the menu item that corresponds with it
	 * @param text
	 * @return
	 */
	public DataSongMenuItem getMenuItemFromString(String text) {
		return mMenuItemsHash.get(text);
	}

}
