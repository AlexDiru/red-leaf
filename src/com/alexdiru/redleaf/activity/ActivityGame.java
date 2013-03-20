package com.alexdiru.redleaf.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import com.alexdiru.redleaf.GameView;

/**
 * The activity for the game
 * @author Alex
 *
 */
public class ActivityGame extends ActivityCommon {

	private GameView mGameView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		//This happens when the game has been left idleA, just switch to previous activity
		try {
			mGameView = new GameView(this);
			setContentView(mGameView);
		} catch (Exception ex) {
			finish();
		}
	}

	@Override
	protected void onPause() {
		Log.d(getClass().getName(), "onPause");
		super.onPause();
		
		mGameView.pauseGame();
	}

	@Override
	protected void onStop() {
		Log.d(getClass().getName(), "onStop");
		super.onStop();
		
		mGameView.pauseGame();
	}

	@Override
	protected void onRestart() {
		Log.d(getClass().getName(), "onRestart");
		super.onRestart();
		mGameView.resumeGame();
	}

	@Override
	protected void onResume() {
		Log.d(getClass().getName(), "onResume");
		super.onResume();

		mGameView.resumeGame();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (mGameView != null)
			mGameView.dispose();
		
		mGameView = null;
	}
}
