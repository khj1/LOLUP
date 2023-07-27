package com.lolup.member.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.lolup.member.exception.InvalidEmailException;
import com.lolup.member.exception.InvalidMemberNameException;

class MemberTest {

	@DisplayName("회원을 생성한다.")
	@Test
	void createMember() {
		assertDoesNotThrow(() -> new Member("name", "aaa@bbb.ccc", Role.USER, "picture"));
	}

	@DisplayName("회원 이름이 1~16자가 아니라면 예외가 발생한다.")
	@ValueSource(strings = {"", " ", "aaaaaaaaaaaaaaaaa"})
	@ParameterizedTest
	void createMemberWithInvalidName(final String invalidName) {
		assertThatThrownBy(() -> new Member(invalidName, "aaa@bbb.ccc", Role.USER, "picture"))
				.isInstanceOf(InvalidMemberNameException.class);
	}

	@DisplayName("이메일 형식이 올바르지 않은 경우 예외가 발생한다.")
	@ValueSource(strings = {"", " ", "aaa@bbb.", "aaa@bbb", "aaabbb.ccc", "aaa@", "@bbb.ccc", "aaa"})
	@ParameterizedTest
	void createMemberWithInvalidEmail(final String invalidEmail) {
		assertThatThrownBy(() -> new Member("name", invalidEmail, Role.USER, "picture"))
				.isInstanceOf(InvalidEmailException.class);
	}
}
