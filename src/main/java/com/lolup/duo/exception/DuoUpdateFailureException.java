package com.lolup.duo.exception;

public class DuoUpdateFailureException extends RuntimeException {

	public DuoUpdateFailureException() {
		super("해당 모집글의 작성자와 일치하지 않아 내용을 변경할 수 없습니다.");
	}
}
