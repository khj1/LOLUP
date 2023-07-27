package com.lolup.member.exception;

import com.lolup.member.domain.Member;

public class InvalidMemberNameException extends RuntimeException {

	public InvalidMemberNameException() {
		super(
				String.format("회원 이름은 %d자 이상 %d자 이하여야 합니다.",
						Member.MIN_NAME_LENGTH,
						Member.MAX_NAME_LENGTH
				)
		);
	}
}
