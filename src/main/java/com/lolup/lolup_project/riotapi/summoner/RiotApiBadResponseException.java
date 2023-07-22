package com.lolup.lolup_project.riotapi.summoner;

public class RiotApiBadResponseException extends RuntimeException {

	public RiotApiBadResponseException() {
		super("라이엇 서버 내부에서 오류가 발생했습니다.");
	}
}
