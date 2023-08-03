package com.lolup.riot.summoner.exception;

public class RiotInternalServerError extends RuntimeException {

	public RiotInternalServerError() {
		super("라이엇 서버 내부에서 오류가 발생했습니다.");
	}
}
