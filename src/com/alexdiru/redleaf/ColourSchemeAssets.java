package com.alexdiru.redleaf;

import java.io.IOException;

import com.alexdiru.redleaf.android.StrokePaint;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;

/**
 * Stores assets dependent on the colour scheme chosen
 * @author Alex
 *
 */
public class ColourSchemeAssets {

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

	public ColourSchemeAssets(ColourScheme colourScheme, int tapBoxWidth, int tapBoxHeight) {
		setupPaints();

		int gapBetweenTapBoxes = (UtilsScreenSize.getScreenWidth() - (DataTapAreas.TAP_AREA_WIDTH * 4)) / 5;

		try {
			mBackgroundBitmap = loadBackground(colourScheme.mBackground, colourScheme.mBackgroundAlpha);
			mBackgroundStarBitmap = loadBackground(colourScheme.mBackgroundStar, colourScheme.mBackgroundAlpha);
			
			//Star power bitmap
			mStarPowerBitmap = BitmapFactory.decodeStream(Utils.getActivity().getAssets().open(colourScheme.mStarPower));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// Load all of the tap box bitmaps (non-hold and hold) and note bitmaps
			for (int i = 0; i < 4; i++) {
				LOST[i] = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(Utils.getActivity().getAssets().open(colourScheme.mTap[i])), UtilsScreenSize.scaleX(tapBoxWidth),
						UtilsScreenSize.scaleY(tapBoxHeight), false);
				LOSTInverse[i] = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(Utils.getActivity().getAssets().open(colourScheme.mTapHold[i])), UtilsScreenSize.scaleX(tapBoxWidth),
						UtilsScreenSize.scaleY(tapBoxHeight), false);
				mNoteBitmap[i] = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(Utils.getActivity().getAssets().open(colourScheme.mNote[i])), UtilsScreenSize.scaleX(DataSong.NOTESIZE),
						UtilsScreenSize.scaleY(DataSong.NOTESIZE), false);
				mNoteHeldBitmap[i] = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(Utils.getActivity().getAssets().open(colourScheme.mNoteHeld[i])), UtilsScreenSize.scaleX(DataSong.NOTESIZE),
						UtilsScreenSize.scaleY(DataSong.NOTESIZE), false);
				mNoteStarBitmap[i] = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(Utils.getActivity().getAssets().open(colourScheme.mNoteStar[i])), UtilsScreenSize.scaleX(DataSong.NOTESIZE),
						UtilsScreenSize.scaleY(DataSong.NOTESIZE), false);

				// We have to adjust the paint bitmap offset
				Matrix matrix = new Matrix();
				int offsetWidth = gapBetweenTapBoxes * (i + 1) + DataTapAreas.TAP_AREA_WIDTH * i;
				matrix.preTranslate(offsetWidth, 0);

				// Load the held and unheld notes
				Bitmap held = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(Utils.getActivity().getAssets().open(colourScheme.mNoteStreamHeld[i])), UtilsScreenSize.scaleX(DataSong.NOTESIZE),
						UtilsScreenSize.scaleY(DataSong.NOTESIZE), false);
				Bitmap unheld = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(Utils.getActivity().getAssets().open(colourScheme.mNoteStreamUnheld[i])),
						UtilsScreenSize.scaleX(DataSong.NOTESIZE), UtilsScreenSize.scaleY(DataSong.NOTESIZE), false);

				BitmapShader unheldShader = new BitmapShader(unheld, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
				unheldShader.setLocalMatrix(matrix);

				BitmapShader heldShader = new BitmapShader(held, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
				heldShader.setLocalMatrix(matrix);

				mHoldLineUnheldPaint[i] = new Paint();
				mHoldLineUnheldPaint[i].setShader(unheldShader);

				mHoldLineHeldPaint[i] = new Paint();
				mHoldLineHeldPaint[i].setShader(heldShader);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public void drawTapBox(Canvas canvas, DataBoundingBox tapbox, int tapboxIndex, boolean touched) {
		tapbox.drawWithBitmap(canvas, touched ? LOSTInverse[tapboxIndex] : LOST[tapboxIndex], null, true, UtilsScreenSize.scaleX(6));
	}

	public Paint getHoldLineUnheldPaint(int position) {
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
	public void setupBackgroundsWithTapboxes(DataBoundingBox[] tapBoundingBoxes) {
		mBackgroundBitmap = drawTapboxesOnBackground(mBackgroundBitmap, tapBoundingBoxes);
		mBackgroundStarBitmap = drawTapboxesOnBackground(mBackgroundStarBitmap, tapBoundingBoxes);
	}
	
	private Bitmap drawTapboxesOnBackground(Bitmap backgroundBitmap, DataBoundingBox[] tapBoundingBoxes){
		Bitmap storedBackground = backgroundBitmap;
		backgroundBitmap = Bitmap.createBitmap(UtilsScreenSize.getScreenWidth(), UtilsScreenSize.getScreenHeight(), Config.RGB_565);
		Canvas canvas = new Canvas(backgroundBitmap);
		canvas.drawBitmap(storedBackground, 0,0,null);

		for (int t = 0; t < tapBoundingBoxes.length; t++)
			drawTapBox(canvas, tapBoundingBoxes[t], t, false);
		
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
}
