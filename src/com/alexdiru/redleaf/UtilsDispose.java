package com.alexdiru.redleaf;

import android.graphics.Bitmap;

import com.alexdiru.redleaf.interfaces.IDisposable;

public abstract class UtilsDispose {
	public static void disposeBitmap(Bitmap bitmap) {
		try {
			bitmap.recycle();
			bitmap = null;
		} catch (Exception ex) {
		}
	}

	public static void disposeBitmaps(Bitmap[] arr) {
		for (int i = 0; i < arr.length; i++)
			disposeBitmap(arr[i]);
		arr = null;
	}

	public static void dispose(IDisposable d) {
		if (d != null) {
			d.dispose();
			d = null;
		}
	}
	
	public static void disposeAll(IDisposable[] d) {
		for (int i = 0; i < d.length; i++)
			dispose(d[i]);
		d = null;
	}
}
