package com.alexdiru.redleaf;

import android.os.Bundle;
import android.util.Log;

public class ActivityGame extends ActivityCommon {
	/** Called when the activity is first created. */

	private static final String TAG = ActivityGame.class.getSimpleName();

	private GameView mGameView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mGameView = new GameView(this);
		setContentView(mGameView);

	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();

		mGameView.pauseGame();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop");
		super.onStop();
		mGameView.pauseGame();
	}

	@Override
	protected void onRestart() {
		Log.d(TAG, "onRestart");
		super.onRestart();
		mGameView.resumeGame();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();

		mGameView.resumeGame();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mGameView.cleanup();
		mGameView = null;
	}
}
