package com.lolup.auth.exception;

public class GoogleAuthorizationException extends RuntimeException {

	public GoogleAuthorizationException() {
		super("인가코드 또는 리다이렉트 URI가 유효하지 않거나 구글 서버에서 문제가 발생했습니다.");
	}
}
