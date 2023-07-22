package com.lolup.lolup_project.auth.exception;

public class InvalidTokenException extends RuntimeException {

	public InvalidTokenException(final String message) {
		super(message);
	}
}
