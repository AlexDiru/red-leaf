package com.alexdiru.redleaf.activity;

import android.app.Activity;
import android.os.Bundle;

import com.alexdiru.redleaf.R;

/** The activity for viewing the credits of the game
 * @author Alex */
public class ActivityCredits extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCommon.create(this);
		
		setContentView(R.layout.activity_credits);
	}
	
}