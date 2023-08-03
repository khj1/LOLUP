package com.lolup.riot.match.exception;

public class InvalidMatchIdException extends RuntimeException {

	public InvalidMatchIdException() {
		super("유효하지 않은 MATCH ID 입니다.");
	}
}
