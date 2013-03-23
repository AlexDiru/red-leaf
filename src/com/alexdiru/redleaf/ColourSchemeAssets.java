package com.alexdiru.redleaf;

import java.io.IOException;
import java.util.Arrays;

import com.alexdiru.redleaf.android.StrokePaint;
import com.alexdiru.redleaf.interfaces.IDisposable;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;

/**
 * Stores assets dependent on the colour scheme chosen
 * @author Alex
 *
 */
public class ColourSchemeAssets implements IDisposable {

	// Tapbox bitmaps
	private Bitmap[] LOST = new Bitmap[4];
	private Bitmap[] LOSTInverse = new Bitmap[4];

	// Note bitmap
	private Bitmap[] mNoteBitmap = new Bitmap[4];
	
	private Bitmap[] mNoteStarBitmap = new Bitmap[4];

	private Bitmap[] mNoteHeldBitmap = new Bitmap[4];

	// Background
	private Bitmap mBackgroundBitmap;
	private Bitmap mBackgroundStarBitmap;

	private Paint[] mHoldLineUnheldPaint = new Paint[4];
	private Paint[] mHoldLineHeldPaint = new Paint[4];
	private Paint[] mHoldLineStarUnheldPaint = new Paint[4];
	
	private Bitmap mStarPowerBitmap;
	

	/** Paint used for drawing the combo counter */
	private StrokePaint mComboPaint = new StrokePaint();
	
	/** Paint used for drawing the score */
	private StrokePaint mScorePaint = new StrokePaint();
	
	/**
	 * Paint used for drawing the accuracy percentage
	 */
	private StrokePaint mAccuracyPaint = new StrokePaint();
	
	/** Paint used for drawing the multiplier */
	private StrokePaint mMultiplierPaint = new StrokePaint();
	
	/** Paint used for drawing the countdown timer */
	private StrokePaint mCountdownPaint = new StrokePaint();
	
	private static Bitmap loadBackground(String filePath, int alpha) throws IOException {
		
		Bitmap backgroundBitmap;
		
		// When loading the background it must be scaled so that the height is the same as the
		// screen height
		// But the width must also be scaled in the same ratio that the height was scaled in
		// Then the bitmap must be centred when positioned
		backgroundBitmap = Bitmap.createBitmap(UtilsScreenSize.getScreenWidth(), UtilsScreenSize.getScreenHeight(), Config.ARGB_8888);
		Bitmap originalBackground;
		originalBackground = BitmapFactory.decodeStream(Utils.getActivity().getAssets().open(filePath));
	
		int scaledWidth = (int) (originalBackground.getWidth() * (UtilsScreenSize.getScreenHeight() / (float) originalBackground.getHeight()));
		Canvas background = new Canvas(backgroundBitmap);
		background.drawBitmap(Bitmap.createScaledBitmap(originalBackground, scaledWidth, UtilsScreenSize.getScreenHeight(), false),
				UtilsScreenSize.getScreenWidth() / 2 - scaledWidth / 2, 0, null);
		
		//Create a white rectangle to fade out the background
		background.drawARGB(255 - alpha, 255,255,255);
		
		return backgroundBitmap;
	}

	public ColourSchemeAssets(ColourScheme colourScheme, int tapBoxHeight) {
		setupPaints();

		//Tapboxes
		for (int i = 0; i < 4; i++) {
			LOST[i] = UtilsScreenSize.loadBitmapInRatioFromHeight(colourScheme.mTap[i], UtilsScreenSize.scaleY(tapBoxHeight));
			LOSTInverse[i] = UtilsScreenSize.loadBitmapInRatioFromHeight(colourScheme.mTapHold[i], UtilsScreenSize.scaleY(tapBoxHeight));
		}


		//Adjust the width of the tapboxes to maintain the same ratio as used in the image that represents them
		DataSong.NOTESIZE = DataPlayer.TAP_AREA_WIDTH = UtilsScreenSize.getWidthInRatio(DataPlayer.TAP_AREA_HEIGHT, LOST[0].getWidth(), LOST[0].getHeight());
		//New the width has been implemented we can calculate the gap
		DataPlayer.TAP_AREA_GAP = (720 - (DataPlayer.TAP_AREA_WIDTH * 4))/5;
		
		//Pixel gap between tap boxes
		int gapBetweenTapBoxes = (UtilsScreenSize.getScreenWidth() - (int)UtilsScreenSize.scaleX(DataPlayer.TAP_AREA_WIDTH * 4)) / 5;

		try {
			mBackgroundBitmap = loadBackground(colourScheme.mBackground, colourScheme.mBackgroundAlpha);
			mBackgroundStarBitmap = loadBackground(colourScheme.mBackgroundStar, colourScheme.mBackgroundAlpha);
		} catch (Exception e) {
			e.printStackTrace();
		}
			mStarPowerBitmap = UtilsScreenSize.loadBitmapInRatioFromHeight(colourScheme.mStarPower, UtilsScreenSize.scaleX(300));

			// Load all of the tap box bitmaps (non-hold and hold) and note bitmaps
			for (int i = 0; i < 4; i++) {
				mNoteBitmap[i] = UtilsScreenSize.loadBitmapInRatioFromHeight(colourScheme.mNote[i], UtilsScreenSize.scaleY(DataSong.NOTESIZE));
				mNoteHeldBitmap[i] = UtilsScreenSize.loadBitmapInRatioFromHeight(colourScheme.mNoteHeld[i], UtilsScreenSize.scaleY(DataSong.NOTESIZE));
				mNoteStarBitmap[i] = UtilsScreenSize.loadBitmapInRatioFromHeight(colourScheme.mNoteStar[i], UtilsScreenSize.scaleY(DataSong.NOTESIZE));
				
				// We have to adjust the paint bitmap offset
				Matrix matrix = new Matrix();
				int offsetWidth = gapBetweenTapBoxes * (i + 1) + UtilsScreenSize.scaleX( DataPlayer.TAP_AREA_WIDTH * i);
				matrix.preTranslate(offsetWidth, 0);

				// Load the held and unheld notes
				Bitmap held = UtilsScreenSize.loadBitmapInRatioFromHeight(colourScheme.mNoteStreamHeld[i], UtilsScreenSize.scaleY(DataSong.NOTESIZE));
				Bitmap unheldStar = UtilsScreenSize.loadBitmapInRatioFromHeight(colourScheme.mNoteStreamUnheldStar[i], UtilsScreenSize.scaleY(DataSong.NOTESIZE));
				Bitmap unheld = UtilsScreenSize.loadBitmapInRatioFromHeight(colourScheme.mNoteStreamUnheld[i], UtilsScreenSize.scaleY(DataSong.NOTESIZE));

				BitmapShader unheldShader = new BitmapShader(unheld, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
				unheldShader.setLocalMatrix(matrix);
				
				BitmapShader unheldStarShader = new BitmapShader(unheldStar, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
				unheldStarShader.setLocalMatrix(matrix);

				BitmapShader heldShader = new BitmapShader(held, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
				heldShader.setLocalMatrix(matrix);

				mHoldLineUnheldPaint[i] = new Paint();
				mHoldLineUnheldPaint[i].setShader(unheldShader);
				mHoldLineUnheldPaint[i].setAlpha(170);
				
				mHoldLineStarUnheldPaint[i] = new Paint();
				mHoldLineStarUnheldPaint[i].setShader(unheldStarShader);
				mHoldLineStarUnheldPaint[i].setAlpha(170);

				mHoldLineHeldPaint[i] = new Paint();
				mHoldLineHeldPaint[i].setShader(heldShader);
				mHoldLineHeldPaint[i].setAlpha(170);
			}
		
		colourScheme.dispose();
	}
	
	private void setupPaints() {
		mComboPaint.setTextSize(UtilsScreenSize.scaleFontSize(70));
		mComboPaint.setTextAlign(Align.CENTER);
		mComboPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
		mComboPaint.setStrokeWidth(UtilsScreenSize.scaleFontSize(6));

		mScorePaint.setTextSize(UtilsScreenSize.scaleFontSize(55));
		mScorePaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
		mScorePaint.setStrokeWidth(UtilsScreenSize.scaleFontSize(6));
		mScorePaint.setTextAlign(Align.CENTER);
		mScorePaint.setARGB(220, 255, 255, 255);
		mScorePaint.setStrokeARGB(220, 0, 0, 0);
		
		mAccuracyPaint.setTextSize(UtilsScreenSize.scaleFontSize(55));
		mAccuracyPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
		mAccuracyPaint.setStrokeWidth(UtilsScreenSize.scaleFontSize(6));
		mAccuracyPaint.setARGB(220, 255, 255, 255);
		mAccuracyPaint.setStrokeARGB(220, 0, 0, 0);
		mAccuracyPaint.setTextAlign(Align.RIGHT);
		
		mMultiplierPaint.setTextSize(UtilsScreenSize.scaleFontSize(55));
		mMultiplierPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
		mMultiplierPaint.setStrokeWidth(UtilsScreenSize.scaleFontSize(6));
		mMultiplierPaint.setARGB(220, 255, 255, 255);
		mMultiplierPaint.setStrokeARGB(220, 0, 0, 0);
		

		mCountdownPaint.setTextSize(UtilsScreenSize.scaleFontSize(110));
		mCountdownPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
		mCountdownPaint.setStrokeWidth(UtilsScreenSize.scaleFontSize(12));
		mCountdownPaint.setTextAlign(Align.CENTER);
		mCountdownPaint.setARGB(220, 255, 255, 255);
		mCountdownPaint.setStrokeARGB(220, 0, 0, 0);
	}

	public Bitmap getBackground(boolean starPowerActive) {
		if(starPowerActive)
			return mBackgroundStarBitmap;
		
		return mBackgroundBitmap;
	}

	public Bitmap getNote(int position) {
		return mNoteBitmap[position];
	}
	
	public Bitmap getNoteStar(int position) {
		return mNoteStarBitmap[position];
	}

	public Bitmap getTapBox(int position) {
		return LOST[position];
	}

	public Bitmap getTapBoxHeld(int position) {
		return LOSTInverse[position];
	}

	public void drawBackground(Canvas canvas) {
		canvas.drawBitmap(mBackgroundBitmap, 0, 0, null);
	}

	public Paint getHoldLineUnheldPaint(int position, boolean isStarNote) {
		if (isStarNote)
			return mHoldLineStarUnheldPaint[position];
		return mHoldLineUnheldPaint[position];
	}

	public Paint getHoldLineHeldPaint(int position) {
		return mHoldLineHeldPaint[position];
	}

	public Bitmap getNoteHeld(int position) {
		return mNoteHeldBitmap[position];
	}

	/**
	 * Draws the tapboxes to the same bitmap as the background
	 * @param tapBoundingBoxes
	 */
	public void setupBackgroundsWithTapboxes(DataTapBox[] tapBoundingBoxes) {
		mBackgroundBitmap = drawTapboxesOnBackground(mBackgroundBitmap, tapBoundingBoxes);
		mBackgroundStarBitmap = drawTapboxesOnBackground(mBackgroundStarBitmap, tapBoundingBoxes);
	}
	
	private Bitmap drawTapboxesOnBackground(Bitmap backgroundBitmap, DataTapBox[] tapBoundingBoxes){
		Bitmap storedBackground = backgroundBitmap;
		backgroundBitmap = Bitmap.createBitmap(UtilsScreenSize.getScreenWidth(), UtilsScreenSize.getScreenHeight(), Config.RGB_565);
		Canvas canvas = new Canvas(backgroundBitmap);
		canvas.drawBitmap(storedBackground, 0,0,null);

		for (int t = 0; t < tapBoundingBoxes.length; t++)
			tapBoundingBoxes[t].render(canvas);
		
		return backgroundBitmap;
	}

	private void updateComboPaints(StrokePaint comboPaint,  int streak) {
		int opacity = 190;
		
		//Set colours depending on the streak
		if (streak < 20) {
			comboPaint.setARGB(opacity, 255, 220, 0);
			comboPaint.setStrokeARGB(opacity, 255, 192, 0);
		} else if (streak < 40) {
			comboPaint.setARGB(opacity, 255, 160, 0);
			comboPaint.setStrokeARGB(opacity, 255, 120, 0);
		} else if (streak < 80) {
			comboPaint.setARGB(opacity, 255, 128, 0);
			comboPaint.setStrokeARGB(opacity, 255, 64, 0);
		} else if (streak < 150) {
			comboPaint.setARGB(opacity, 255, 85, 0);
			comboPaint.setStrokeARGB(opacity, 255, 20, 0);
		} else if (streak < 250) {
			comboPaint.setARGB(opacity, 255, 64, 0);
			comboPaint.setStrokeARGB(opacity, 255, 12, 0);
		} else if (streak < 500) {
			comboPaint.setARGB(opacity, 255, 12, 0);
			comboPaint.setStrokeARGB(opacity, 225	, 0, 0);
		}
	}

	public Bitmap getStarPower() {
		return mStarPowerBitmap;
	}
	
	public StrokePaint getComboPaint(int streak) {
		updateComboPaints(mComboPaint, streak);
		return mComboPaint;
	}
	
	public StrokePaint getScorePaint() {
		return mScorePaint;
	}
	
	public StrokePaint getAccuracyPaint() {
		return mAccuracyPaint;
	}
	
	public StrokePaint getMultiplierPaint() {
		return mMultiplierPaint;
	}
	
	public StrokePaint getCountdownPaint() {
		return mCountdownPaint;
	}

	@Override
	public void dispose() {
		UtilsDispose.disposeBitmaps(LOST);
		UtilsDispose.disposeBitmaps(LOSTInverse);
		UtilsDispose.disposeBitmaps(mNoteBitmap);
		UtilsDispose.disposeBitmaps(mNoteStarBitmap);
		UtilsDispose.disposeBitmaps(mNoteHeldBitmap);
		UtilsDispose.disposeBitmap(mBackgroundBitmap);
		UtilsDispose.disposeBitmap(mBackgroundStarBitmap);
		UtilsDispose.disposeBitmap(mStarPowerBitmap);
		UtilsDispose.dispose(mComboPaint);
		UtilsDispose.dispose(mScorePaint);
		UtilsDispose.dispose(mAccuracyPaint);
		UtilsDispose.dispose(mMultiplierPaint);
		UtilsDispose.dispose(mCountdownPaint);
		Arrays.fill(mHoldLineUnheldPaint, null);
		Arrays.fill(mHoldLineHeldPaint, null);
		Arrays.fill(mHoldLineStarUnheldPaint, null);
		LOST = null;
		LOSTInverse = null;
		mNoteBitmap = null;
		mNoteStarBitmap = null;
		mNoteHeldBitmap = null;
		mBackgroundBitmap = null;
		mBackgroundStarBitmap = null;
		mStarPowerBitmap = null;
		mComboPaint = null;
		mAccuracyPaint = null;
		mMultiplierPaint = null;
		mCountdownPaint = null;
		mHoldLineUnheldPaint = null;
		mHoldLineHeldPaint = null;
		mHoldLineStarUnheldPaint = null;
	}
}
