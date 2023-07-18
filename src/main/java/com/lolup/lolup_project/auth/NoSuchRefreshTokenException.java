package com.lolup.lolup_project.auth;

public class NoSuchRefreshTokenException extends RuntimeException {

	public NoSuchRefreshTokenException(final String message) {
		super(message);
	}

	public NoSuchRefreshTokenException() {
		super("존재하지 않는 리프레시 토큰입니다.");
	}
}
