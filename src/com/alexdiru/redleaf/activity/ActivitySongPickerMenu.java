package com.alexdiru.redleaf.activity;

import java.io.IOException;

import com.alexdiru.redleaf.DataSong;
import com.alexdiru.redleaf.DataSongMenuItem;
import com.alexdiru.redleaf.R;
import com.alexdiru.redleaf.Utils;
import com.alexdiru.redleaf.UtilsStepmaniaConvertor;
import com.alexdiru.redleaf.DataSong.DataSongDifficulty;
import com.alexdiru.redleaf.R.id;
import com.alexdiru.redleaf.R.layout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ActivitySongPickerMenu extends ActivityCommon implements OnItemClickListener, DialogInterface.OnClickListener {

	private ListView mListView;
	private FileIOSongListParser mSongListParser;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_songpicker);

		// Setup the list view
		// Load all the songs from file
		mSongListParser = new FileIOSongListParser("SongList.txt");
		mListView = (ListView) findViewById(R.id.menusongpicker_listview);
		mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mSongListParser.getMenuItemsToDisplay()));
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> a, View v, int position, long id) {

		// Get the note file of the selected song
		DataSongMenuItem menuItem = mSongListParser.getMenuItemFromString(((TextView) v).getText().toString());
		final String noteFile = menuItem.mNoteFile;

		// Check if the file exists
		boolean fileExists = true;
		try {
			Utils.getActivity().getAssets().open(noteFile);
		} catch (IOException e) {
			fileExists = false;
		}

		if (fileExists) {
			//(new DataSong(noteFile));
			

			// Choose difficulties dialog box
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Choose a difficulty").setTitle(menuItem.mSongName);

			builder.setPositiveButton("Hard",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Utils.setCurrentSong(new DataSong(noteFile));
							//Utils.setCurrentSong(UtilsStepmaniaConvertor.stepmaniaToRedLeaf("World Mad.ssc",DataSong.DataSongDifficulty.HARD));
							Utils.getCurrentSong().mDifficulty = DataSong.DataSongDifficulty.HARD;
							Utils.switchActivity(ActivityGame.class);
						}
					});

			builder.setNeutralButton("Medium",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Utils.setCurrentSong(UtilsStepmaniaConvertor.stepmaniaToRedLeaf("World Mad.ssc",DataSong.DataSongDifficulty.MEDIUM));
							Utils.getCurrentSong().mDifficulty = DataSong.DataSongDifficulty.MEDIUM;
							Utils.switchActivity(ActivityGame.class);
						}
					});
			
			builder.setNegativeButton("Easy",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Utils.setCurrentSong(UtilsStepmaniaConvertor.stepmaniaToRedLeaf("World Mad.ssc",DataSong.DataSongDifficulty.EASY));
							Utils.getCurrentSong().mDifficulty = DataSong.DataSongDifficulty.EASY;
							Utils.switchActivity(ActivityGame.class);
						}
					});
			
			builder.show();

			
		}
		else {

			// 1. Instantiate an AlertDialog.Builder with its constructor
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			// 2. Chain together various setter methods to set the dialog
			// characteristics
			builder.setMessage("Sorry this song hasn't been implemented yet :(").setTitle("Sorry");

			// 3. Get the AlertDialog from create()
			AlertDialog dialog = builder.create();

			dialog.show();
		}

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub

	}

}
