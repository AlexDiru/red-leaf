package com.alexdiru.redleaf;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.alexdiru.redleaf.interfaces.IDisposable;
import com.alexdiru.redleaf.interfaces.IRenderable;

public class DataNoteHoldLine implements IRenderable, IDisposable{

	private DataNote mParentNote;
	private int mY1Position;
	private int mY2Position;

	/** Only need one rectangle to share between all of the notes */
	private static Rect mHoldLineRect = new Rect();
	
	public DataNoteHoldLine(DataNote parent)
	{
		mParentNote = parent;
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	public void update(float songSpeed, int currentTime)
	{
		mY1Position = UtilsScreenSize.scaleY(DataPlayer.mUnscaledTapBoxY - (int)((mParentNote.getEndTime() - currentTime ) * songSpeed));
		if (mY1Position < 0)
			mY1Position = 0;
		
		mY2Position = mParentNote.getScaledYPosition() + DataNote.mNotePixelHeight / 2;
		if (mY2Position > UtilsScreenSize.getScreenHeight())
			mY2Position = UtilsScreenSize.getScreenHeight();
	}

	@Override
	public void render(Canvas canvas) {
		// Something has gone wrong
		if (mHoldLineRect == null)
			return;

		// Create the rectangle
		mHoldLineRect.left = mParentNote.getScaledXPosition();
		mHoldLineRect.top = mY1Position;
		mHoldLineRect.right = mParentNote.getScaledXPosition() + DataNote.mNotePixelHeight;
		mHoldLineRect.bottom = mY2Position;

		// Draw the hold line
		canvas.drawRect(mHoldLineRect, mParentNote.isHeld() ? ColourSchemeAssets.getHoldLineHeldPaint(mParentNote.getPosition()) :
					ColourSchemeAssets.getHoldLineUnheldPaint(mParentNote.getPosition(), mParentNote.isStarNote()));
		
	}

}
