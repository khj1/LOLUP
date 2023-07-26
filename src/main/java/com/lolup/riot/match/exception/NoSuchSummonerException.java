package com.lolup.riot.match.exception;

public class NoSuchSummonerException extends RuntimeException {

	public NoSuchSummonerException() {
		super("존재하지 않는 소환사입니다.");
	}
}
