 package com.alexdiru.redleaf;

import android.content.Context;
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

import com.alexdiru.redleaf.activity.ActivityGame;
import com.alexdiru.redleaf.android.MusicManager;
import com.alexdiru.redleaf.android.ScoreDialog;
import com.alexdiru.redleaf.interfaces.IDisposable;

public class GameView extends SurfaceView implements
		SurfaceHolder.Callback, OnTouchListener, IDisposable {

	private GameThread mGameThread;
	private MusicManager mMusicManager;
	private DataPlayer mTapAreas;

	// Text Paints
	private Paint mTextPaint = new Paint();

	/** The song being played */
	private DataSong mSong;

	/** Current time that the song is at, assigned in the update method, used in the draw method */
	private int mCurrentTime;

	// / Debug Variables ///
	// Variables for recording the FPS
	private long mPreviousTime = 0, mCurrentTimeFPS = 0, mFPS, mTotalFPS = 0, mLoopCount = 0;

	private DataCountdownTimer mCountdownTimer;
	
	private GameState mGameState = new GameState();

	public GameView(Context context) {
		super(context);
		setFocusable(true);
		requestFocus();
		getHolder().addCallback(this);

		setOnTouchListener(this);

		mGameThread = new GameThread(this);
		mMusicManager = new MusicManager();
		
		
		mSong = Utils.getCurrentSong();
		mTapAreas = new DataPlayer();
		mCountdownTimer = new DataCountdownTimer(ColourSchemeAssets.getCountdownPaint());
		
		setupTextPaints();

		mGameState.set(GameState.STATE_COUNTDOWN);
		
		//Get initial notes to render so they can be shown during the countdown
		mSong.updateNotes(0, 1280);
	}

	private void setupTextPaints() {
		mTextPaint.setColor(Color.BLACK);
		mTextPaint.setTextSize(40);
	}

	// Used to release any resources.
	@Override
	public void dispose() {
		Log.d(getClass().getName(), "dispose");
		mMusicManager.dispose();
		mMusicManager = null;

		mGameThread.mRunning = false;

		removeCallbacks(mGameThread);
		mGameThread = null;

		setOnTouchListener(null);

		SurfaceHolder holder = getHolder();
		holder.removeCallback(this);
	}

	/* Setters and Getters */

	public void setThread(GameThread newThread) {

		mGameThread = newThread;

		setOnTouchListener(this);

		setClickable(true);
		setFocusable(true);
	}

	/* Screen functions */

	// ensure that we go into pause state if we go out of focus
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		Log.d(ActivityGame.class.getName(), "onWindowFocusChanged");
		if (mGameThread != null) {
			if (!hasWindowFocus) {
				pauseGame();
			} else
				resumeGame();
		}
	}

	public void pauseGame() {

		if (mGameState.get() == GameState.STATE_GAME)
			if (mMusicManager != null)
				mMusicManager.pause();

		mGameThread.mPaused = true;
	}

	public void resumeGame() {

		if (mGameState.get() == GameState.STATE_GAME)
			if (mMusicManager != null)
				mMusicManager.resume();

		if (mGameThread != null)
			mGameThread.mPaused = false;
		
		//This needs to be done so the elapsed time isn't the time in which the game was paused
		mCurrentTimeFPS = SystemClock.elapsedRealtime();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(ActivityGame.class.getName(), "surfaceCreated");
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

	/* Need to stop the GameThread if the surface is destroyed Remember this doesn't need to happen
	 * when app is paused on even stopped. */
	public void surfaceDestroyed(SurfaceHolder arg0) {
		Log.d(ActivityGame.class.getName(), "surfaceDestroyed");

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

		if (mGameState.get() == GameState.STATE_GAME) {
	
			mFPS = (int) (1000 * ((float) 1 / (float) (mCurrentTimeFPS - mPreviousTime)));
			mTotalFPS += mFPS;
			mLoopCount++;
	
			// Update the timer
			if (mTapAreas != null) {
				mCurrentTime = mMusicManager.getPlayPosition();
				mTapAreas.update(mCurrentTime);
				mSong.updateNotes(mCurrentTime, 1280);
			}
	
			//Check if song is over
			if (mMusicManager.isSongOver()) {
				mGameState.set(GameState.STATE_SCOREDISPLAY);

				synchronized (this) {
					ScoreDialog.show(mTapAreas.getScore());
					UtilsDispose.dispose(mTapAreas);
					mTapAreas = null;
					mSong = null;
				}
			}
		} else if (mGameState.get() == GameState.STATE_COUNTDOWN) {

			if (mCountdownTimer.hasFinished()) {
				mGameState.set(GameState.STATE_GAME);
				mMusicManager.play();
			}
				
			if (mPreviousTime != 0)
				mCountdownTimer.update(mCurrentTimeFPS - mPreviousTime);
		}
	}

	public void draw(Canvas canvas) {

		// Tap boxes
		if (mTapAreas != null)
			mTapAreas.render(canvas);

		
		// Notes
		if (mSong != null)
			mSong.render(canvas);
		
		if (mGameState.get() == GameState.STATE_GAME) {
			// Text
			drawScore(canvas);
			// drawSongName(canvas);
			drawAccuracy(canvas);
			// drawStreak(canvas);
			drawMultiplier(canvas);
			drawFPS(canvas);
			drawCombo(canvas);
		} else if (mGameState.get() == GameState.STATE_COUNTDOWN) {
			mCountdownTimer.render(canvas);
		}
	}

	private void drawCombo(Canvas canvas) {
		if (mTapAreas.getStreak() < 4)
			return;

		StringBuilder sb = UtilsString.getStringBuilder();
		UtilsString.appendInteger(mTapAreas.getStreak());
		sb.append(Utils.getActivity().getString(R.string.game_combosuffix));
		sb.getChars(0, sb.length(), UtilsString.getChars(), 0);
		ColourSchemeAssets.getComboPaint(mTapAreas.getStreak())
				.drawText(canvas, UtilsString.getChars(), 0, sb.length(), UtilsScreenSize.getScreenWidth() / 2, UtilsScreenSize.getScreenHeight() / 2);

	}

	private void drawStreak(Canvas canvas) {
		StringBuilder sb = UtilsString.getStringBuilder();
		sb.append(Utils.getActivity().getString(R.string.game_streakprefix));
		UtilsString.appendInteger(mTapAreas.getStreak());
		sb.getChars(0, sb.length(), UtilsString.getChars(), 0);
		canvas.drawText(UtilsString.getChars(), 0, sb.length(), 30, 200, mTextPaint);
	}

	private void drawMultiplier(Canvas canvas) {
		StringBuilder sb = UtilsString.getStringBuilder();

		if (mTapAreas.isStarPowerActive())
			UtilsString.appendInteger(16);
		else
			UtilsString.appendInteger(mTapAreas.getMultiplier());

		sb.append(Utils.getActivity().getString(R.string.game_multipliersuffix));
		sb.getChars(0, sb.length(), UtilsString.getChars(), 0);
		ColourSchemeAssets.getMultiplierPaint().drawText(canvas, UtilsString.getChars(), 0, sb.length(), UtilsScreenSize.scaleY(30), UtilsScreenSize.scaleY(80));
	}

	private void drawAccuracy(Canvas canvas) {
		StringBuilder sb = UtilsString.getStringBuilder();
		// sb.append(Utils.getActivity().getString(R.string.game_accuracyprefix));
		if (mTapAreas.getTappedCount() + mTapAreas.getMissedCount() < 1)
			UtilsString.appendInteger(100);
		else
			UtilsString.appendInteger((int) (100.0 * ((float) mTapAreas.getTappedCount() / (float) (mTapAreas.getTappedCount() + mTapAreas.getMissedCount()))));
		sb.append(Utils.getActivity().getString(R.string.game_accuracysuffix));
		sb.getChars(0, sb.length(), UtilsString.getChars(), 0);
		ColourSchemeAssets.getAccuracyPaint().drawText(canvas, UtilsString.getChars(), 0, sb.length(), UtilsScreenSize.getScreenWidth() -  UtilsScreenSize.scaleY(30), UtilsScreenSize.scaleY(80));
	}

	private void drawScore(Canvas canvas) {
		StringBuilder sb = UtilsString.getStringBuilder();
		sb.append(Utils.getActivity().getString(R.string.game_scoreprefix));
		UtilsString.appendInteger(mTapAreas.getScore());
		sb.getChars(0, sb.length(), UtilsString.getChars(), 0);
		ColourSchemeAssets.getScorePaint().drawText(canvas, UtilsString.getChars(), 0, sb.length(), UtilsScreenSize.getScreenWidth() / 2, UtilsScreenSize.scaleY(80));
	}

	private void drawSongName(Canvas canvas) {
		StringBuilder sb = UtilsString.getStringBuilder();
		sb.append(Utils.getActivity().getString(R.string.game_songnameprefix));
		sb.append(Utils.getCurrentSong().mSongName);
		sb.getChars(0, sb.length(), UtilsString.getChars(), 0);
		canvas.drawText(UtilsString.getChars(), 0, sb.length(), 30, 100, mTextPaint);
	}

	private void drawFPS(Canvas canvas) {
		if (mLoopCount == 0)
			return;
		
		StringBuilder sb = UtilsString.getStringBuilder();
		sb.append(Utils.getActivity().getString(R.string.game_fpsprefix));
		UtilsString.appendInteger((int) mFPS);
		sb.append(Utils.getActivity().getString(R.string.game_fpsdivider));
		UtilsString.appendInteger((int) (mTotalFPS / mLoopCount));
		sb.getChars(0, sb.length(), UtilsString.getChars(), 0);
		canvas.drawText(UtilsString.getChars(), 0, sb.length(), 30, 300, mTextPaint);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (mGameState.get() == GameState.STATE_GAME) {
		int actionCode = event.getAction() & MotionEvent.ACTION_MASK;
		int touchOrderID = event.getAction() >> MotionEvent.ACTION_POINTER_ID_SHIFT;

		if (touchOrderID > 1)
			return false;

		switch (actionCode) {
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_DOWN:
			mTapAreas.handleTouchDown((int) event.getX(touchOrderID), (int) event.getY(touchOrderID), touchOrderID, mCurrentTime);
			debugMSLogTouch((int) event.getX(touchOrderID), (int) event.getY(touchOrderID));
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
		}
		return true;
	}
	
	private void debugMSLogTouch(int x, int y) {
		if (y < UtilsScreenSize.getScreenHeight() >> 1)
			Log.d("msLog", String.valueOf(mCurrentTime));
	}

}
