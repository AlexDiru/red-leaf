package com.alexdiru.redleaf;

import com.alexdiru.redleaf.activity.ActivityGame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameThread extends Thread {

	private SurfaceHolder mSurfaceHolder;
	private GameView mGameView;
	public boolean mRunning;
	public boolean mPaused;

	public int mPreviousMusicPosition;

	public GameThread(GameView gameView) {
		super();
		mSurfaceHolder = gameView.getHolder();
		mGameView = gameView;
	}

	@Override
	public void run() {
		Canvas canvas;

		while (mRunning) {

			if (mPaused)
				continue;

			canvas = null;

			try {
				canvas = mSurfaceHolder.lockCanvas(null);
				
				
				synchronized (mSurfaceHolder) {
					if (mGameView != null && canvas != null) {
						// Update the game
						try {
							update();
						} catch (Exception ex) {
							Log.d("exception", "update exception");
						}
						
						// Render the game
						//try {
							draw(canvas);
						//} catch (Exception ex) {
						//	Log.d("exception", "draw exception");
						//}
					}
				}
			} finally {

				if (canvas != null) {
					if (mSurfaceHolder != null)
						mSurfaceHolder.unlockCanvasAndPost(canvas);

				}
			}
		}
	}

	private void update() {
		mGameView.update();
	}

	private void draw(Canvas canvas) {
		mGameView.draw(canvas);
	}
}
