package com.alexdiru.redleaf;

import android.graphics.Canvas;
import android.graphics.Color;
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
							update();

							// Render the game
							draw(canvas);
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
		canvas.drawColor(Color.BLACK);
		mGameView.draw(canvas);
	}
}
