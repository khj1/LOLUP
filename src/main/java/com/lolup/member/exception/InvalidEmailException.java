package com.lolup.member.exception;

public class InvalidEmailException extends RuntimeException {

	public InvalidEmailException() {
		super("유효하지 않은 이메일 형식입니다.");
	}
}
