package com.alexdiru.redleaf.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.alexdiru.redleaf.R;
import com.alexdiru.redleaf.Utils;
import com.alexdiru.redleaf.UtilsScreenSize;

public class ActivityMainMenu extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCommon.create(this);
		UtilsScreenSize.initialise(getWindowManager().getDefaultDisplay());
		
		setContentView(R.layout.activity_menumain);

		getWindow().setBackgroundDrawable(new MenuBackground("menu_background.png"));
		
		addButtonListeners();

	}
	
	private void addButtonListeners() {
		Button playButton = (Button)findViewById(R.id.playButton);
		playButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Utils.switchActivity(ActivitySongPickerMenu.class);
			}
		});
		
		Button exitButton = (Button)findViewById(R.id.exitButton);
		
		final Activity activity = this;
		
		exitButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				activity.finish();
			}
		});
		
		Button creditsButton = (Button)findViewById(R.id.creditsButton);
		creditsButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Utils.switchActivity(ActivityCredits.class);
			}
		});

	}
}

