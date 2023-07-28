package com.lolup.member.domain;

import static com.lolup.common.fixture.MemberFixture.EMAIL;
import static com.lolup.common.fixture.MemberFixture.NAME;
import static com.lolup.common.fixture.MemberFixture.PICTURE;
import static com.lolup.common.fixture.MemberFixture.소환사_등록_회원;
import static com.lolup.common.fixture.MemberFixture.신규_회원;
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

	public static final String UPDATED_NAME = "updatedName";
	public static final String UPDATED_EMAIL = "ddd@eee.fff";
	public static final String UPDATED_PICTURE = "updatedPicture";

	@DisplayName("회원을 생성한다.")
	@Test
	void create() {
		assertDoesNotThrow(() -> new Member(NAME, EMAIL, Role.USER, PICTURE));
	}

	@DisplayName("회원 이름이 1~16자가 아니라면 예외가 발생한다.")
	@ValueSource(strings = {"", " ", "aaaaaaaaaaaaaaaaa"})
	@ParameterizedTest
	void createWithInvalidName(final String invalidName) {
		assertThatThrownBy(() -> new Member(invalidName, EMAIL, Role.USER, PICTURE))
				.isInstanceOf(InvalidMemberNameException.class);
	}

	@DisplayName("이메일 형식이 올바르지 않은 경우 예외가 발생한다.")
	@ValueSource(strings = {"", " ", "aaa@bbb.", "aaa@bbb", "aaabbb.ccc", "aaa@", "@bbb.ccc", "aaa"})
	@ParameterizedTest
	void createWithInvalidEmail(final String invalidEmail) {
		assertThatThrownBy(() -> new Member(NAME, invalidEmail, Role.USER, PICTURE))
				.isInstanceOf(InvalidEmailException.class);
	}

	@DisplayName("회원 정보를 수정한다.")
	@Test
	void update() {
		Member member = 신규_회원();

		member.update(UPDATED_NAME, UPDATED_EMAIL, UPDATED_PICTURE);

		assertThat(member)
				.extracting("name", "email", "picture")
				.containsExactly(UPDATED_NAME, UPDATED_EMAIL, UPDATED_PICTURE);
	}

	@DisplayName("회원 정보 수정 시 입력한 이름이 1~16자가 아니라면 예외가 발생한다.")
	@ValueSource(strings = {"", " ", "aaaaaaaaaaaaaaaaa"})
	@ParameterizedTest
	void updateWithInvalidName(final String invalidName) {
		Member member = 신규_회원();

		assertThatThrownBy(() -> member.update(invalidName, UPDATED_EMAIL, UPDATED_PICTURE))
				.isInstanceOf(InvalidMemberNameException.class);
	}

	@DisplayName("회원 정보 수정 시 입력한 이메일의 형식이 올바르지 않다면 예외가 발생한다.")
	@ValueSource(strings = {"", " ", "aaa@bbb.", "aaa@bbb", "aaabbb.ccc", "aaa@", "@bbb.ccc", "aaa"})
	@ParameterizedTest
	void updateWithInvalidEmail(final String invalidEmail) {
		Member member = 신규_회원();

		assertThatThrownBy(() -> member.update(UPDATED_NAME, invalidEmail, UPDATED_PICTURE))
				.isInstanceOf(InvalidEmailException.class);
	}

	@DisplayName("소환사 이름을 변경할 수 있다.")
	@Test
	void changeSummonerName() {
		Member member = 소환사_등록_회원();

		member.changeSummonerName(UPDATED_NAME);

		assertThat(member.getSummonerName()).isEqualTo(UPDATED_NAME);
	}

	@DisplayName("변경 할 소환사 이름이 1~16자가 아니라면 예외가 발생한다.")
	@ValueSource(strings = {"", " ", "aaaaaaaaaaaaaaaaa"})
	@ParameterizedTest
	void changeInvalidSummonerName(final String invalidSummonerName) {
		Member member = 소환사_등록_회원();

		assertThatThrownBy(() -> member.changeSummonerName(invalidSummonerName))
				.isInstanceOf(InvalidSummonerNameException.class);
	}
}
