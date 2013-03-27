package com.alexdiru.redleaf.exception;

public class SongNotOverException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SongNotOverException(String message) {
        super(message);
    }

    public SongNotOverException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
