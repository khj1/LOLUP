package com.lolup.lolup_project.member.exception;

public class NoSuchMemberException extends RuntimeException {

	public NoSuchMemberException() {
		super("존재하지 않는 회원입니다.");
	}
}
