package com.dropmate.exceptions;

public class EmailFoundException extends Exception {
	
    private static final long serialVersionUID = 1L;

	public EmailFoundException() {
        super("Email is already present with this name, try with another name");
    }

    public EmailFoundException(String msg) {
        super((msg));
    }
}
