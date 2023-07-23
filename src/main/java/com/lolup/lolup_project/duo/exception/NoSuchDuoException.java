package com.lolup.lolup_project.duo.exception;

public class NoSuchDuoException extends RuntimeException {

	public NoSuchDuoException(final String message) {
		super(message);
	}

	public NoSuchDuoException() {
		super("존재하지 않는 듀오입니다.");
	}
}
