package com.alexdiru.redleaf;

import android.graphics.Canvas;
import android.graphics.Paint;

public class UtilsString {

	private static final StringBuilder mStringBuilder = new StringBuilder(100);
	private static final char[] mChars = new char[100];
	private static int[] mDigitBuffer = new int[10];
	private static String[] mDigits = {"0","1","2","3","4","5","6","7","8","9" };
	
	public static StringBuilder getStringBuilder() {
		mStringBuilder.setLength(0);
		return mStringBuilder;
	}
	
	/** Appends an integer to the string builder without allocation */
	public static void appendInteger(int i) {
		int index = 0;
		while (i > 0) {
			mDigitBuffer[index] = i % 10;
			i /= 10;
			index++;
		}
		while (index > 0)
			mStringBuilder.append(mDigits[mDigitBuffer[--index]]);
	}
	
	public static char[] getChars() {
		return mChars;
	}
}
