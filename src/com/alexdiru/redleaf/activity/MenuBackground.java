package com.alexdiru.redleaf.activity;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;

import com.alexdiru.redleaf.Utils;
import com.alexdiru.redleaf.UtilsScreenSize;
import com.alexdiru.redleaf.exception.ResourceNotExistant;

/** Provides the functionality for centered tile mode */
public class MenuBackground extends Drawable {

	private BitmapShader mBitmapShader;
	private Rect mBackgroundRect;
	private Paint mPaint;
	
	public MenuBackground(String assetFile) {
		Bitmap src;
		
		try{
			src = BitmapFactory.decodeStream(Utils.getActivity().getAssets().open(assetFile));
		} catch (IOException ex) {
			throw new ResourceNotExistant(assetFile);
		}
		
		//Matrix to transpose shader
		Matrix matrix = new Matrix();
		matrix.setTranslate((UtilsScreenSize.getScreenWidth() - src.getWidth()) >> 1,(UtilsScreenSize.getScreenHeight() - src.getHeight()) >> 1);
		
		mBitmapShader = new BitmapShader(src, TileMode.MIRROR, TileMode.MIRROR);
		mBitmapShader.setLocalMatrix(matrix);
		mBackgroundRect = new Rect(0,0,UtilsScreenSize.getScreenWidth(), UtilsScreenSize.getScreenHeight());
		mPaint = new Paint();
		mPaint.setShader(mBitmapShader);
		
	}
	
	@Override
	public void draw(Canvas canvas) {
		canvas.drawRect(mBackgroundRect, mPaint);
	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAlpha(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setColorFilter(ColorFilter arg0) {
		// TODO Auto-generated method stub
		
	}

}
