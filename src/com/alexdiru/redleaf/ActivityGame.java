package com.alexdiru.redleaf;

import android.os.Bundle;
import android.util.Log;

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

		mGameView = new GameView(this);
		setContentView(mGameView);

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
		mGameView.cleanup();
		mGameView = null;
	}
}
