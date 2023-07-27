package com.lolup.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.lolup.member.exception.InvalidEmailException;
import com.lolup.member.exception.InvalidMemberNameException;
import com.lolup.member.exception.InvalidSummonerNameException;

class MemberTest {

	@DisplayName("회원을 생성한다.")
	@Test
	void create() {
		assertDoesNotThrow(() -> new Member("name", "aaa@bbb.ccc", Role.USER, "picture"));
	}

	@DisplayName("회원 이름이 1~16자가 아니라면 예외가 발생한다.")
	@ValueSource(strings = {"", " ", "aaaaaaaaaaaaaaaaa"})
	@ParameterizedTest
	void createWithInvalidName(final String invalidName) {
		assertThatThrownBy(() -> new Member(invalidName, "aaa@bbb.ccc", Role.USER, "picture"))
				.isInstanceOf(InvalidMemberNameException.class);
	}

	@DisplayName("이메일 형식이 올바르지 않은 경우 예외가 발생한다.")
	@ValueSource(strings = {"", " ", "aaa@bbb.", "aaa@bbb", "aaabbb.ccc", "aaa@", "@bbb.ccc", "aaa"})
	@ParameterizedTest
	void createWithInvalidEmail(final String invalidEmail) {
		assertThatThrownBy(() -> new Member("name", invalidEmail, Role.USER, "picture"))
				.isInstanceOf(InvalidEmailException.class);
	}

	@DisplayName("회원 정보를 수정한다.")
	@Test
	void update() {
		Member member = new Member("name", "aaa@bbb.ccc", Role.USER, "picture");

		member.update("updatedName", "ddd@eee.fff", "updatedPicture");

		assertThat(member)
				.extracting("name", "email", "picture")
				.containsExactly("updatedName", "ddd@eee.fff", "updatedPicture");
	}

	@DisplayName("회원 정보 수정 시 입력한 이름이 1~16자가 아니라면 예외가 발생한다.")
	@ValueSource(strings = {"", " ", "aaaaaaaaaaaaaaaaa"})
	@ParameterizedTest
	void updateWithInvalidName(final String invalidName) {
		Member member = new Member("name", "aaa@bbb.ccc", Role.USER, "picture");

		assertThatThrownBy(() -> member.update(invalidName, "ddd@eee.fff", "updatedPicture"))
				.isInstanceOf(InvalidMemberNameException.class);
	}

	@DisplayName("회원 정보 수정 시 입력한 이메일의 형식이 올바르지 않다면 예외가 발생한다.")
	@ValueSource(strings = {"", " ", "aaa@bbb.", "aaa@bbb", "aaabbb.ccc", "aaa@", "@bbb.ccc", "aaa"})
	@ParameterizedTest
	void updateWithInvalidEmail(final String invalidEmail) {
		Member member = new Member("name", "aaa@bbb.ccc", Role.USER, "picture");

		assertThatThrownBy(() -> member.update("updatedName", invalidEmail, "updatedPicture"))
				.isInstanceOf(InvalidEmailException.class);
	}

	@DisplayName("소환사 이름을 변경할 수 있다.")
	@Test
	void changeSummonerName() {
		Member member = new Member("name", "aaa@bbb.ccc", Role.USER, "picture", "summonerName");

		member.changeSummonerName("updatedName");

		assertThat(member.getSummonerName()).isEqualTo("updatedName");
	}

	@DisplayName("변경 할 소환사 이름이 1~16자가 아니라면 예외가 발생한다.")
	@ValueSource(strings = {"", " ", "aaaaaaaaaaaaaaaaa"})
	@ParameterizedTest
	void changeInvalidSummonerName(final String invalidSummonerName) {
		Member member = new Member("name", "aaa@bbb.ccc", Role.USER, "picture", "summonerName");

		assertThatThrownBy(() -> member.changeSummonerName(invalidSummonerName))
				.isInstanceOf(InvalidSummonerNameException.class);
	}
}
