package com.alexdiru.redleaf;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/** Provides commoon functionality each activity has This involves setting the current activity and
 * context to the static Utils class
 * @author Alex */
public class ActivityCommon extends Activity {

	/** Passes the context and activity to the Utils class */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		System.gc();

		// Visual appearance
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Pass data to static class
		Utils.setContext(getBaseContext());
		Utils.setActivity(this);
	}
}
