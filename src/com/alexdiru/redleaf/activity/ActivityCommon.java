package com.alexdiru.redleaf.activity;

import com.alexdiru.redleaf.Utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/** Provides commoon functionality each activity has This involves setting the current activity and
 * context to the static Utils class
 * @author Alex */
public abstract class ActivityCommon {

	/** Passes the context and activity to the Utils class */
	public static void create(Activity activity) {
		
		System.gc();

		// Visual appearance
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// Pass data to static class
		Utils.setContext(activity.getBaseContext());
		Utils.setActivity(activity);
	}
}
