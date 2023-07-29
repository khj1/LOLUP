package com.lolup.config.oauth.exception;

public class NoSuchSocialPlatformException extends RuntimeException {

	public NoSuchSocialPlatformException() {
		super("지원하지 않는 소셜 로그인 플랫폼입니다.");
	}
}
