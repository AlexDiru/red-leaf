package com.alexdiru.redleaf.exception;

public class ValueNotSetException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ValueNotSetException(String message) {
        super(message);
    }

    public ValueNotSetException(String message, Throwable throwable) {
        super(message, throwable);
    }

}