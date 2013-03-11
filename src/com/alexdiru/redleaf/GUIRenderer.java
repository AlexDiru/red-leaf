package com.alexdiru.redleaf;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;

public class GUIRenderer {

	//Tapbox bitmaps
	private Bitmap[] LOST = new Bitmap[4];
	private Bitmap[] LOSTInverse = new Bitmap[4];
	
	//Note bitmap
	private Bitmap[] mNoteBitmap = new Bitmap[4];
	
	private Bitmap[] mNoteHeldBitmap = new Bitmap[4];
	
	//Background
	private Bitmap mBackgroundBitmap;
	

	private Paint[] mHoldLineUnheldPaint = new Paint[4];
	private Paint[] mHoldLineHeldPaint = new Paint[4];

	public GUIRenderer(ColourScheme colourScheme, int tapBoxWidth, int tapBoxHeight) {

		int gapBetweenTapBoxes = (UtilsScreenSize.getScreenWidth() - (DataTapAreas.TAP_AREA_WIDTH*4))/5;
		
		try {
			//Load all of the tap box bitmaps (non-hold and hold) and note bitmaps
			for (int i = 0; i < 4; i++) {
				LOST[i] = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(Utils.getActivity().getAssets().open(colourScheme.mTap[i])), UtilsScreenSize.scaleX(tapBoxWidth), UtilsScreenSize.scaleY(tapBoxHeight), false);
				LOSTInverse[i] = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(Utils.getActivity().getAssets().open(colourScheme.mTapHold[i])), UtilsScreenSize.scaleX(tapBoxWidth), UtilsScreenSize.scaleY(tapBoxHeight), false);
				mNoteBitmap[i] = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(Utils.getActivity().getAssets().open(colourScheme.mNote[i])), UtilsScreenSize.scaleX(DataSong.NOTESIZE), UtilsScreenSize.scaleY(DataSong.NOTESIZE), false);
				mNoteHeldBitmap[i] = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(Utils.getActivity().getAssets().open(colourScheme.mNoteHeld[i])), UtilsScreenSize.scaleX(DataSong.NOTESIZE), UtilsScreenSize.scaleY(DataSong.NOTESIZE), false);

				//When loading the background it must be scaled so that the height is the same as the screen height
				//But the width must also be scaled in the same ratio that the height was scaled in
				//Then the bitmap must be centred when positioned
				mBackgroundBitmap = Bitmap.createBitmap(UtilsScreenSize.getScreenWidth(), UtilsScreenSize.getScreenHeight(), Config.RGB_565);
				Bitmap originalBackground = BitmapFactory.decodeStream(Utils.getActivity().getAssets().open(colourScheme.mBackground));
				int scaledWidth = (int)(originalBackground.getWidth() * (UtilsScreenSize.getScreenHeight()/(float)originalBackground.getHeight()));
				Canvas background = new Canvas(mBackgroundBitmap);
				background.drawBitmap(Bitmap.createScaledBitmap(originalBackground, scaledWidth, UtilsScreenSize.getScreenHeight(), false),
									  UtilsScreenSize.getScreenWidth()/2 - scaledWidth/2,0,null);
				
				//We have to adjust the paint bitmap offset
				Matrix matrix = new Matrix();
				int offsetWidth = gapBetweenTapBoxes * (i + 1) + DataTapAreas.TAP_AREA_WIDTH * i;
				matrix.preTranslate(offsetWidth, 0);
				
				//Load the held and unheld notes
				Bitmap held = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(Utils.getActivity().getAssets().open(colourScheme.mNoteStreamHeld[i])), UtilsScreenSize.scaleX(DataSong.NOTESIZE), UtilsScreenSize.scaleY(DataSong.NOTESIZE), false);
				Bitmap unheld = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(Utils.getActivity().getAssets().open(colourScheme.mNoteStreamUnheld[i])), UtilsScreenSize.scaleX(DataSong.NOTESIZE), UtilsScreenSize.scaleY(DataSong.NOTESIZE), false);

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
	
	public Bitmap getBackground() {
		return mBackgroundBitmap;
	}
	
	public Bitmap getNote(int position) {
		return mNoteBitmap[position];
	}
	
	public Bitmap getTapBox(int position) {
		return LOST[position];
	}

	public Bitmap getTapBoxHeld(int position) {
		return LOSTInverse[position];
	}

	public void drawBackground(Canvas canvas) {
		canvas.drawBitmap(mBackgroundBitmap, 0, 0,null);
	}

	public void drawTapBox(Canvas canvas,DataBoundingBox tapbox, int tapboxIndex, boolean touched) {
		tapbox.drawWithBitmap(canvas, touched ? LOSTInverse[tapboxIndex] : LOST[tapboxIndex], null);
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
}