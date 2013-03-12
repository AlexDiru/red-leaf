package com.alexdiru.redleaf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class GameView extends SurfaceView implements
		SurfaceHolder.Callback, OnTouchListener {
	
	private static final String SONG_NAME_PREFIX = Utils.getActivity().getString(R.string.game_songnameprefix);

	private GameThread mGameThread;
	public MusicManager mMusicManager;
	private DataTapAreas mTapAreas;

	private Bitmap mBackground;

	private long mPreviousTime, mCurrentTimeFPS, mFPS, mTotalFPS = 0, mLoopCount = 0;

	private Paint mTextPaint = new Paint();

	public static final int TAPCIRCLES_Y = (int)(1280/1.3061);
	
	private DataSong mSong;

	public GameView(Context context) {
		super(context);
		setFocusable(true);
		requestFocus();
		getHolder().addCallback(this);

		setOnTouchListener(this);

		mGameThread = new GameThread(this);

		mMusicManager = new MusicManager(Utils.getCurrentSong().mMusicFile);
		
		mSong = Utils.getCurrentSong();
		
		mSong.mMusicManager = mMusicManager;

		mTapAreas = new DataTapAreas(Utils.getCurrentSong());
		Utils.getCurrentSong().setTapAreas(mTapAreas);

		//Helper.getCurrentSong().generateRandomNotes(1);

		mTextPaint.setColor(Color.BLACK);
		mTextPaint.setTextSize(40);
	}


	// Used to release any resources.
	public void cleanup() {
		mMusicManager.cleanup();
		mMusicManager = null;

		mGameThread.mRunning = false;

		removeCallbacks(mGameThread);
		mGameThread = null;

		setOnTouchListener(null);

		SurfaceHolder holder = getHolder();
		holder.removeCallback(this);
	}

	/*
	 * Setters and Getters
	 */

	public void setThread(GameThread newThread) {

		mGameThread = newThread;

		setOnTouchListener(this);

		setClickable(true);
		setFocusable(true);
	}

	/*
	 * Screen functions
	 */

	// ensure that we go into pause state if we go out of focus
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if (mGameThread != null) {
			if (!hasWindowFocus) {
				pauseGame();
			}
		}
	}

	public void pauseGame() {

		if (mMusicManager != null)
			mMusicManager.pause();

		mGameThread.mPaused = true;
	}

	public void resumeGame() {

		if (mMusicManager != null)
			mMusicManager.resume();

		if (mGameThread != null)
			mGameThread.mPaused = false;
	}

	public void surfaceCreated(SurfaceHolder holder) {

		if (mGameThread != null) {
			mGameThread.mRunning = true;

			if (mGameThread.getState() == Thread.State.NEW) {
				// Just start the new thread
				mGameThread.start();
			}
			else {
				if (mGameThread.getState() == Thread.State.TERMINATED) {
					// Set up and start a new thread with the old thread as seed
					mGameThread = new GameThread(this);
					mGameThread.mRunning = true;
					mGameThread.start();
				}
			}
		} else {
			mGameThread = new GameThread(this);
		}
	}

	// Always called once after surfaceCreated. Tell the GameThread the actual
	// size
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	/*
	 * Need to stop the GameThread if the surface is destroyed Remember this
	 * doesn't need to happen when app is paused on even stopped.
	 */
	public void surfaceDestroyed(SurfaceHolder arg0) {

		// Stop the thread from running its update and draw methods
		if (mGameThread != null)
			mGameThread.mRunning = false;

		// Join the UI thread and game thread together
		boolean retry = true;
		while (retry) {
			try {
				if (mGameThread != null)
					mGameThread.join();

				retry = false;
			} catch (InterruptedException e) {
				// Keep waiting for the thread to finish
			}
		}
	}

	public void update() {

		
		mPreviousTime = mCurrentTimeFPS;
		mCurrentTimeFPS = SystemClock.elapsedRealtime();

		mFPS = (int) (1000 * ((float) 1 / (float) (mCurrentTimeFPS - mPreviousTime)));
		mTotalFPS += mFPS;
		mLoopCount++;
		
		//Log.d("fps", mFPS + " / " + (mTotalFPS/mLoopCount));
		
		//Update the timer
		if (mTapAreas != null) {

			mTapAreas.update();
			Utils.getCurrentSong().updateNotes(mMusicManager.getPlayPosition(), UtilsScreenSize.getScreenHeight(), TAPCIRCLES_Y);
		}

	}

	public void draw(Canvas canvas) {

		//Background
		
		//Tap boxes
		if (mTapAreas != null)
			mTapAreas.draw(canvas);

		//Notes
		mSong.renderNotes(canvas, mTapAreas);
		
		// Text
		drawScore(canvas);
		drawSongName(canvas);
		drawAccuracy(canvas);
		drawStreak(canvas);
		drawMultiplier(canvas);
		drawFPS(canvas);
	}
	
	private void drawStreak(Canvas canvas) {
		StringBuilder sb = UtilsString.getStringBuilder();
		sb.append(Utils.getActivity().getString(R.string.game_streakprefix));
		UtilsString.appendInteger(mTapAreas.getStreak());
		sb.getChars(0, sb.length(), UtilsString.getChars(), 0);
		canvas.drawText(UtilsString.getChars(), 0, sb.length(), 100, 200, mTextPaint);
	}
	
	private void drawMultiplier(Canvas canvas) {
		StringBuilder sb = UtilsString.getStringBuilder();
		sb.append(Utils.getActivity().getString(R.string.game_multiplierprefix));
		UtilsString.appendInteger(mTapAreas.getMultiplier());
		sb.getChars(0, sb.length(), UtilsString.getChars(), 0);
		canvas.drawText(UtilsString.getChars(), 0, sb.length(), 100, 250, mTextPaint);
	}
	
	private void drawAccuracy(Canvas canvas) {
		StringBuilder sb = UtilsString.getStringBuilder();
		sb.append(Utils.getActivity().getString(R.string.game_accuracyprefix));
		if (mTapAreas.getTappedCount() + mTapAreas.getMissedCount() < 1)
			UtilsString.appendInteger(100);
		else
			UtilsString.appendInteger((int)(100.0*((float)mTapAreas.getTappedCount()/(float)(mTapAreas.getTappedCount() + mTapAreas.getMissedCount()))));
		sb.append(Utils.getActivity().getString(R.string.game_accuracysuffix));
		sb.getChars(0, sb.length(), UtilsString.getChars(), 0);
		canvas.drawText(UtilsString.getChars(),0, sb.length(),100,150, mTextPaint);
	}
	
	private void drawScore(Canvas canvas) {
		StringBuilder sb = UtilsString.getStringBuilder();
		sb.append(Utils.getActivity().getString(R.string.game_scoreprefix));
		UtilsString.appendInteger(mTapAreas.getScore());
		sb.getChars(0, sb.length(), UtilsString.getChars(), 0);
		canvas.drawText(UtilsString.getChars(), 0, sb.length(), 100, 50, mTextPaint);
	}
	
	private void drawSongName(Canvas canvas) {
		StringBuilder sb = UtilsString.getStringBuilder();
		sb.append(Utils.getActivity().getString(R.string.game_songnameprefix));
		sb.append(Utils.getCurrentSong().mSongName);
		sb.getChars(0, sb.length(), UtilsString.getChars(), 0);
		canvas.drawText(UtilsString.getChars(), 0, sb.length(), 100, 100, mTextPaint);
	}
	
	private void drawFPS(Canvas canvas) {
		StringBuilder sb = UtilsString.getStringBuilder();
		sb.append(Utils.getActivity().getString(R.string.game_fpsprefix));
		UtilsString.appendInteger((int)mFPS);
		sb.append(Utils.getActivity().getString(R.string.game_fpsdivider));
		UtilsString.appendInteger((int) (mTotalFPS / mLoopCount));
		sb.getChars(0, sb.length(), UtilsString.getChars(), 0);
		canvas.drawText(UtilsString.getChars(), 0, sb.length(), 100, 300, mTextPaint);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		int actionCode = event.getAction() & MotionEvent.ACTION_MASK;
		int touchOrderID = event.getAction() >> MotionEvent.ACTION_POINTER_ID_SHIFT;

		if (touchOrderID > 1)
			return false;

		switch (actionCode) {
		case MotionEvent.ACTION_DOWN:
			mTapAreas.handleTouchDown((int) event.getX(touchOrderID), (int) event.getY(touchOrderID), touchOrderID);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			mTapAreas.handleTouchDown((int) event.getX(touchOrderID), (int) event.getY(touchOrderID), touchOrderID);
			break;
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_CANCEL:
			mTapAreas.handleTouchUp(touchOrderID);
			break;
		case MotionEvent.ACTION_UP:
			mTapAreas.handleAllTouchesUp();
			break;
		default:
		}


		return true;
	}

}
