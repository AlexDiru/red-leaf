package com.alexdiru.redleaf;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public abstract class Utils {

	private static Context mContext;
	private static Activity mActivity;
	private static DataSong mCurrentSong;
	
	public static void switchActivity(Class<?> activity) {

		Intent i = new Intent(mContext, activity);
		
		//Note down the sender activity so we can go back to it
		i.putExtra("SenderActivity", mActivity.getClass().getName().toString());
		
		try {
			mActivity.startActivity(i);
		} catch (ActivityNotFoundException ex) {
			Log.d("switchActivity", "Add activity " + activity.getName() + " to AndroidManifest.xml");
		}
	}
	
	public static void setContext(Context context) {
		mContext = context;
	}
	
	public static void setActivity(Activity activity) {
		mActivity = activity;
	}
	
	public static void setCurrentSong(DataSong dataSong) {
		mCurrentSong = dataSong;
	}
	
	public static DataSong getCurrentSong() {
		return mCurrentSong;
	}
	
	public static Activity getActivity() {
		return mActivity;
	}
	
	public static Context getContext() {
		return mContext;
	}
}
