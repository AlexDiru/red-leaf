package com.alexdiru.redleaf;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ActivityEditorSongPickerMenu extends ActivityCommon implements OnItemClickListener {

	private ListView mListView;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editorsongpicker);

		// Setup the list view
		mListView = (ListView) findViewById(R.id.editorsongpicker_listview);
		mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, UtilsFileIO.getMp3Assets()));
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
		

	}

}
