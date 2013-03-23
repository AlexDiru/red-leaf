package com.alexdiru.redleaf.activity;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alexdiru.redleaf.DataSong;
import com.alexdiru.redleaf.DataSongMenuItem;
import com.alexdiru.redleaf.R;
import com.alexdiru.redleaf.Utils;

public class ActivitySongPickerMenu extends Activity implements OnItemClickListener, DialogInterface.OnClickListener {

	private ListView mListView;
	private FileIOSongListParser mSongListParser;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCommon.create(this);
		setContentView(R.layout.activity_songpicker_layout);
		
		mListView = (ListView)findViewById(R.id.listView);
		
		mListView.setBackgroundDrawable(new MenuBackground("menu_background.png"));
		
		// Setup the list view
		// Load all the songs from file
		loadSongList();

		// mListView = (ListView) findViewById(R.id.menusongpicker_listview);
		mListView.setAdapter(new ArrayAdapter<String>(this,
				R.layout.activity_songpicker,
				mSongListParser.getMenuItemsToDisplay()));
		mListView.setOnItemClickListener(this);
	}

	public void loadSongList() {
		mSongListParser = new FileIOSongListParser("SongList.txt");
	}

	@Override
	public void onItemClick(AdapterView<?> a, View v, int position, long id) {

		// Get the note file of the selected song
		DataSongMenuItem menuItem = mSongListParser
				.getMenuItemFromString(((TextView) v).getText().toString());
		final String noteFile = menuItem.mNoteFile;

		// Check if the file exists
		boolean fileExists = true;
		try {
			Utils.getActivity().getAssets().open(noteFile);
		} catch (IOException e) {
			fileExists = false;
		}

		// if (fileExists) {
		// (new DataSong(noteFile));

		// Choose difficulties dialog box
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Choose a difficulty").setTitle(menuItem.mSongName);

		// After a long wait, clicking the button will do nothing, maybe need to
		// reinitialise the FileIOList on resume??

		if (menuItem.hasHardDifficulty())
			builder.setNegativeButton("Hard",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Utils.setCurrentSong(new DataSong(noteFile, 2));
							Utils.getCurrentSong().mDifficulty = DataSong.DataSongDifficulty.HARD;
							Utils.switchActivity(ActivityGame.class);
						}
					});

		if (menuItem.hasMediumDifficulty())
			builder.setNeutralButton("Medium",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Utils.setCurrentSong(new DataSong(noteFile, 1));
							Utils.getCurrentSong().mDifficulty = DataSong.DataSongDifficulty.MEDIUM;
							Utils.switchActivity(ActivityGame.class);
						}
					});

		if (menuItem.hasEasyDifficulty())
			builder.setPositiveButton("Easy",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Utils.setCurrentSong(new DataSong(noteFile, 0));
							Utils.getCurrentSong().mDifficulty = DataSong.DataSongDifficulty.EASY;
							Utils.switchActivity(ActivityGame.class);
						}
					});

		builder.show();

		// }
		/*
		 * else {
		 * 
		 * // 1. Instantiate an AlertDialog.Builder with its constructor
		 * AlertDialog.Builder builder = new AlertDialog.Builder(this);
		 * 
		 * // 2. Chain together various setter methods to set the dialog //
		 * characteristics
		 * builder.setMessage("Sorry this song hasn't been implemented yet :("
		 * ).setTitle("Sorry");
		 * 
		 * // 3. Get the AlertDialog from create() AlertDialog dialog =
		 * builder.create();
		 * 
		 * dialog.show(); }
		 */

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub

	}

}
