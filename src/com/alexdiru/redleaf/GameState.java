package com.alexdiru.redleaf;

public class GameState {

	public static final int STATE_GAME = 0;
	public static final int STATE_SCOREDISPLAY = 1;
	public static final int STATE_COUNTDOWN = 2;
	
	private int mCurrentState;
	
	public GameState() {
		mCurrentState = STATE_COUNTDOWN;
	}
	
	public void set(int newState) {
		mCurrentState = newState;
	}
	
	public int get() {
		return mCurrentState;
	}

}
