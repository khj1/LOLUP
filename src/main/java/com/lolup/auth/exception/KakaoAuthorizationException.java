package com.lolup.auth.exception;

public class KakaoAuthorizationException extends RuntimeException {

	public KakaoAuthorizationException() {
		super("인가코드 또는 리다이렉트 URI가 유효하지 않거나 카카오 서버에서 문제가 발생했습니다.");
	}
}
