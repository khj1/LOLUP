package com.lolup.auth.exception;

public class NoSuchRefreshTokenException extends RuntimeException {

	public NoSuchRefreshTokenException() {
		super("존재하지 않는 리프레시 토큰입니다.");
	}
}
