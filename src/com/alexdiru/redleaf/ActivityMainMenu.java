package com.alexdiru.redleaf;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ActivityMainMenu extends ActivityCommon {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_menumain);
		
		addButtonListeners();

		UtilsScreenSize.initialise(getWindowManager().getDefaultDisplay());
	}
	
	private void addButtonListeners() {
		TextView playTextView = (TextView)findViewById(R.id.playTextView);
		playTextView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Utils.switchActivity(ActivitySongPickerMenu.class);
			}
		});
		
		TextView editorTextView = (TextView)findViewById(R.id.editorTextView);
		editorTextView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Utils.switchActivity(ActivityEditorSongPickerMenu.class);
			}
		});
		
		TextView creditsTextView = (TextView)findViewById(R.id.creditsTextView);
		creditsTextView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Utils.switchActivity(ActivityCredits.class);
			}
		});
	}
}

