package com.lolup.lolup_project.duo.exception;

public class DuoDeleteFailureException extends RuntimeException {

	public DuoDeleteFailureException() {
		super("해당 듀오 모집글을 제거할 수 없습니다.");
	}
}
