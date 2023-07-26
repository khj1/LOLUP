package com.lolup.riot.summoner.exception;

public class NoGameVersionException extends RuntimeException {

	public NoGameVersionException() {
		super("게임 버전을 불러오지 못했습니다.");
	}
}
