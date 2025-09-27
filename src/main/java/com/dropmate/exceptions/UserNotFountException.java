package com.dropmate.exceptions;

public class UserNotFountException extends Exception {
    private static final long serialVersionUID = 1L;

	public UserNotFountException() {
        super("User with this name is not found in DB");
    }

    public UserNotFountException(String msg) {
        super((msg));
    }
}
