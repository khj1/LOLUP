package com.lolup.auth.exception;

public class KakaoResourceException extends RuntimeException {

	public KakaoResourceException() {
		super("카카오에서 발급받은 엑세스 토큰이 유효하지 않거나 카카오 서버에서 문제가 발생했습니다.");
	}
}
