package com.lolup.auth.exception;

public class EmptyAuthorizationHeaderException extends RuntimeException {

	private static final String NO_AUTHORIZATION_HEADER = "Authorization header가 존재하지 않습니다.";

	public EmptyAuthorizationHeaderException(final String message) {
		super(message);
	}

	public EmptyAuthorizationHeaderException() {
		this(NO_AUTHORIZATION_HEADER);
	}
}
