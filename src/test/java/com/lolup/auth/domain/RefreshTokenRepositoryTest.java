package com.lolup.auth.domain;

import static com.lolup.common.fixture.MemberFixture.소환사_등록_회원;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lolup.common.RepositoryTest;
import com.lolup.member.domain.Member;

class RefreshTokenRepositoryTest extends RepositoryTest {

	private static final String REFRESH_TOKEN = "valid.refresh.token";

	@DisplayName("리프레시 토큰 값으로 리프레시 토큰을 조회할 수 있다.")
	@Test
	void findByRefreshToken() {
		Member member = memberRepository.save(소환사_등록_회원());
		RefreshToken savedRefreshToken = refreshTokenRepository.save(RefreshToken.create(member, REFRESH_TOKEN));

		RefreshToken findRefreshToken = refreshTokenRepository.findByRefreshToken(REFRESH_TOKEN).orElseThrow();

		assertThat(savedRefreshToken).isSameAs(findRefreshToken);
	}

	@DisplayName("리프레스 토큰 값으로 리프레시 토큰을 제거할 수 있다.")
	@Test
	void deleteByMember() {
		Member member = memberRepository.save(소환사_등록_회원());
		refreshTokenRepository.save(RefreshToken.create(member, REFRESH_TOKEN));

		refreshTokenRepository.deleteByRefreshToken(REFRESH_TOKEN);

		assertThat(refreshTokenRepository.findByRefreshToken(REFRESH_TOKEN))
				.isEqualTo(Optional.empty());
	}
}
