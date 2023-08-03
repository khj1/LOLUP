package com.lolup.riot.match.exception;

public class InvalidPuuIdException extends RuntimeException {

	public InvalidPuuIdException() {
		super("유효하지 않은 PUUID입니다.");
	}
}
