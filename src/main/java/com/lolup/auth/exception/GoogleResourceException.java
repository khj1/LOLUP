package com.lolup.auth.exception;

public class GoogleResourceException extends RuntimeException {

	public GoogleResourceException() {
		super("구글에서 발급받은 엑세스 토큰이 유효하지 않거나 구글 서버에서 문제가 발생했습니다.");
	}
}
