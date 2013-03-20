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

import com.alexdiru.redleaf.android.MusicManager;
import com.alexdiru.redleaf.android.ScoreDialog;
import com.alexdiru.redleaf.interfaces.IDisposable;

public class GameView extends SurfaceView implements
		SurfaceHolder.Callback, OnTouchListener, IDisposable {

	private static final String SONG_NAME_PREFIX = Utils.getActivity().getString(R.string.game_songnameprefix);

	public static final int TAPCIRCLES_Y = (int) (1280 / 1.3061);

	private GameThread mGameThread;
	public MusicManager mMusicManager;
	private DataPlayer mTapAreas;

	// Text Paints
	private Paint mTextPaint = new Paint();

	/** The song being played */
	private DataSong mSong;

	/** Current time that the song is at, assigned in the update method, used in the draw method */
	private int mCurrentTime;

	/** Speed at which notes fall */
	//easy 0.7
	//med 0.8
	//hard 1.1
	private float mSongSpeed = 0.85f;

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
		mCountdownTimer = new DataCountdownTimer(mTapAreas.getColourSchemeAssets().getCountdownPaint());
		
		setupTextPaints();

		mGameState.set(GameState.STATE_COUNTDOWN);
		
		//Get initial notes to render so they can be shown during the countdown
		//TODO
		mSong.updateNotes(0,(int) (1280 / mSongSpeed), TAPCIRCLES_Y);
	}

	private void setupTextPaints() {
		mTextPaint.setColor(Color.BLACK);
		mTextPaint.setTextSize(40);
	}

	// Used to release any resources.
	@Override
	public void dispose() {
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
		if (mGameThread != null) {
			if (!hasWindowFocus) {
				pauseGame();
			}
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
		
		if (mGameThread.mPaused)
			return;

		if (mGameState.get() == GameState.STATE_GAME) {
	
			mFPS = (int) (1000 * ((float) 1 / (float) (mCurrentTimeFPS - mPreviousTime)));
			mTotalFPS += mFPS;
			mLoopCount++;
	
			// Update the timer
			if (mTapAreas != null) {
				mCurrentTime = mMusicManager.getPlayPosition();
				mTapAreas.update(mCurrentTime);
				mSong.updateNotes(mCurrentTime, (int) (1280 / mSongSpeed), TAPCIRCLES_Y);
			}
	
			//Check if song is over
			if (!mMusicManager.isPlaying() && !mMusicManager.isPaused()) {
				mGameState.set(GameState.STATE_SCOREDISPLAY);
				ScoreDialog.show(mTapAreas.getScore());
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
		mSong.renderNotes(canvas, mSongSpeed);
		
		if (mGameState.get() == GameState.STATE_GAME) {
			// Text
			drawScore(canvas);
			// drawSongName(canvas);
			drawAccuracy(canvas);
			// drawStreak(canvas);
			drawMultiplier(canvas);
			// drawFPS(canvas);
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
		mTapAreas.getColourSchemeAssets().getComboPaint(mTapAreas.getStreak())
				.drawText(canvas, UtilsString.getChars(), 0, sb.length(), UtilsScreenSize.getScreenWidth() / 2, UtilsScreenSize.getScreenHeight() / 2);

		// STAR POWER
		if (mTapAreas.isStarPowerActive()) {
			sb = UtilsString.getStringBuilder();
			sb.append(Utils.getActivity().getString(R.string.game_starpower));
			sb.getChars(0, sb.length(), UtilsString.getChars(), 0);
			mTapAreas.getColourSchemeAssets().getComboPaint(mTapAreas.getStreak())
					.drawText(canvas, UtilsString.getChars(), 0, sb.length(), UtilsScreenSize.getScreenWidth() / 2, UtilsScreenSize.getScreenHeight() / 2 - UtilsScreenSize.scaleY(60));
		}
		// INSANE
		else if (mTapAreas.getStreak() > 150) {
			sb = UtilsString.getStringBuilder();
			sb.append(Utils.getActivity().getString(R.string.game_highcombo));
			sb.getChars(0, sb.length(), UtilsString.getChars(), 0);
			mTapAreas.getColourSchemeAssets().getComboPaint(mTapAreas.getStreak())
					.drawText(canvas, UtilsString.getChars(), 0, sb.length(), UtilsScreenSize.getScreenWidth() / 2, UtilsScreenSize.getScreenHeight() / 2 - UtilsScreenSize.scaleY(60));
		}

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
		mTapAreas.getColourSchemeAssets().getMultiplierPaint().drawText(canvas, UtilsString.getChars(), 0, sb.length(), UtilsScreenSize.scaleY(30), UtilsScreenSize.scaleY(80));
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
		mTapAreas.getColourSchemeAssets().getAccuracyPaint().drawText(canvas, UtilsString.getChars(), 0, sb.length(), UtilsScreenSize.getScreenWidth() -  UtilsScreenSize.scaleY(30), UtilsScreenSize.scaleY(80));
	}

	private void drawScore(Canvas canvas) {
		StringBuilder sb = UtilsString.getStringBuilder();
		sb.append(Utils.getActivity().getString(R.string.game_scoreprefix));
		UtilsString.appendInteger(mTapAreas.getScore());
		sb.getChars(0, sb.length(), UtilsString.getChars(), 0);
		mTapAreas.getColourSchemeAssets().getScorePaint().drawText(canvas, UtilsString.getChars(), 0, sb.length(), UtilsScreenSize.getScreenWidth() / 2, UtilsScreenSize.scaleY(80));
	}

	private void drawSongName(Canvas canvas) {
		StringBuilder sb = UtilsString.getStringBuilder();
		sb.append(Utils.getActivity().getString(R.string.game_songnameprefix));
		sb.append(Utils.getCurrentSong().mSongName);
		sb.getChars(0, sb.length(), UtilsString.getChars(), 0);
		canvas.drawText(UtilsString.getChars(), 0, sb.length(), 30, 100, mTextPaint);
	}

	private void drawFPS(Canvas canvas) {
		StringBuilder sb = UtilsString.getStringBuilder();
		sb.append(Utils.getActivity().getString(R.string.game_fpsprefix));
		UtilsString.appendInteger((int) mFPS);
		sb.append(Utils.getActivity().getString(R.string.game_fpsdivider));
		UtilsString.appendInteger((int) (mTotalFPS / mLoopCount));
		sb.getChars(0, sb.length(), UtilsString.getChars(), 0);
		canvas.drawText(UtilsString.getChars(), 0, sb.length(), 30, 120, mTextPaint);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		int actionCode = event.getAction() & MotionEvent.ACTION_MASK;
		int touchOrderID = event.getAction() >> MotionEvent.ACTION_POINTER_ID_SHIFT;

		if (touchOrderID > 1)
			return false;

		switch (actionCode) {
		case MotionEvent.ACTION_DOWN:
			mTapAreas.handleTouchDown((int) event.getX(touchOrderID), (int) event.getY(touchOrderID), touchOrderID, mCurrentTime);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			mTapAreas.handleTouchDown((int) event.getX(touchOrderID), (int) event.getY(touchOrderID), touchOrderID, mCurrentTime);
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
