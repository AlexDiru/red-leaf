package com.alexdiru.redleaf;

public abstract class UtilsDP {

	private static Float mDensity = null;
	
	public static int toPx(int dp) {
		if (mDensity == null)
			mDensity = Utils.getContext().getResources().getDisplayMetrics().density;
		
		return Math.round(mDensity * (float)dp);
	}

}
