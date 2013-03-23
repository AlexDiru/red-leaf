package com.alexdiru.redleaf;

import java.util.Arrays;

import com.alexdiru.redleaf.interfaces.IDisposable;

/** Used to mitigate the overhead of hash map allocations forcing the GC
 * 
 * @author Alex */
public class DataTouchMap implements IDisposable {

	/** Maps finger number + position to whether the combination is touched i.e. If secondary finger
	 * is touching position 3 then mTouchMap[4 + 3] will be true */
	private boolean[] mTouchMap = new boolean[8];

	public DataTouchMap() {
		Arrays.fill(mTouchMap, false);
	}

	/** Puts a touch into the touch map
	 * @param index The index of the pointer finger
	 * @param position The position the finger is touching */
	public void put(int index, int position) {
		for (int i = 0; i < 4; i++)
			if (i == position)
				mTouchMap[index * 4 + i] = true;
			else
				mTouchMap[index * 4 + i] = false;
	}

	/** Removes a touch from the touch map
	 * @param index The index of the touch */
	public void remove(int index) {
		// For all of the touches with the desired index set all the touches to
		// false
		for (int i = 0; i < 4; i++)
			if (mTouchMap[index * 4 + i])
				mTouchMap[index * 4 + i] = false;
	}

	/** Sets all the touches to false */
	public void clear() {
		Arrays.fill(mTouchMap, false);
	}

	/** Checks whether the position is touched
	 * @param position The position to check
	 * @return Whether the position is touched */
	public boolean isTouched(int position) {
		return mTouchMap[position] || mTouchMap[4 + position];
	}

	public Integer get(int index) {
		for (int i = 0; i < 4; i++)
			if (mTouchMap[4 * index + i])
				return i;
		return null;
	}
	
	@Override
	public void dispose() {
		mTouchMap = null;
	}
}
