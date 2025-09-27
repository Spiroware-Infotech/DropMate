package com.dropmate.exceptions;

public class UserFoundException extends Exception {
    private static final long serialVersionUID = 1L;

	public UserFoundException() {
        super("User is already present with this name, try with another name");
    }

    public UserFoundException(String msg) {
        super((msg));
    }
}
