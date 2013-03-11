package com.alexdiru.redleaf;

import android.graphics.Rect;
import android.view.MotionEvent;

public class UtilsTouch {

	public static boolean isTouchInsideBoundingBox(int x, int y, Rect rect) {
		return (x <= rect.right && x >= rect.left &&
			y <= rect.bottom && y >= rect.top);
	}

}
