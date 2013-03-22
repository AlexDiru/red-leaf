package com.alexdiru.redleaf.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alexdiru.redleaf.GameView;

/**
 * The activity for the game
 * 
 * @author Alex
 * 
 */
public class ActivityGame extends Activity {

	private GameView mGameView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCommon.create(this);

		// This happens when the game has been left idleA, just switch to
		// previous activity
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
	protected void onUserLeaveHint() {
		Log.d(getClass().getName(), "onUserLeaveHint");
		super.onUserLeaveHint();
	}

	@Override
	protected void onResume() {
		Log.d(getClass().getName(), "onResume");
		super.onResume();

		mGameView.resumeGame();
	}

	@Override
	protected void onStart() {
		Log.d(getClass().getName(), "onStart");
		super.onStart();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		Log.d(getClass().getName(), "onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.d(getClass().getName(), "onNewIntent");
		super.onNewIntent(intent);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.d(getClass().getName(), "onRestoreInstanceState");
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		Log.d(getClass().getName(), "onDestroy");
		super.onDestroy();

		if (mGameView != null)
			mGameView.dispose();

		mGameView = null;
	}
}
