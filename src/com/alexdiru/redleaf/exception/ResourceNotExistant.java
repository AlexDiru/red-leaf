package com.alexdiru.redleaf.exception;

public class ResourceNotExistant extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResourceNotExistant(String message) {
        super(message);
    }

    public ResourceNotExistant(String message, Throwable throwable) {
        super(message, throwable);
    }

}
