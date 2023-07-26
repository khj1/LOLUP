package com.lolup.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lolup.auth.application.dto.AccessTokenResponse;
import com.lolup.auth.domain.RefreshToken;
import com.lolup.auth.exception.NoSuchRefreshTokenException;
import com.lolup.common.ServiceTest;
import com.lolup.member.domain.Member;
import com.lolup.member.domain.Role;

class AuthServiceTest extends ServiceTest {

	@DisplayName("리프레시 토큰이 유효하면 엑세스 토큰을 재발급 한다.")
	@Test
	void refreshValidToken() {
		Member member = memberRepository.save(createMember());
		Long memberId = member.getId();

		String refreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(memberId));
		refreshTokenRepository.save(RefreshToken.create(member, refreshToken));

		AccessTokenResponse accessTokenResponse = authService.refreshToken(refreshToken);

		assertThat(accessTokenResponse.getAccessToken()).isNotEmpty();
	}

	@DisplayName("일치하는 리프레시 토큰이 없으면 예외를 반환한다.")
	@Test
	void refreshInvalidToken() {
		Member member = memberRepository.save(createMember());
		Long memberId = member.getId();

		String refreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(memberId));

		assertThatThrownBy(() -> authService.refreshToken(refreshToken))
				.isInstanceOf(NoSuchRefreshTokenException.class)
				.hasMessage("존재하지 않는 리프레시 토큰입니다.");
	}

	@DisplayName("리프레시 토큰을 제거한다.")
	@Test
	void logout() {
		Member member = memberRepository.save(createMember());
		Long memberId = member.getId();

		String refreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(memberId));
		refreshTokenRepository.save(RefreshToken.create(member, refreshToken));

		refreshTokenRepository.deleteByRefreshToken(refreshToken);

		assertThat(refreshTokenRepository.findByRefreshToken(refreshToken)).isEqualTo(Optional.empty());
	}

	private Member createMember() {
		return Member.builder()
				.name("member")
				.email("aaa@bbb.ccc")
				.role(Role.USER)
				.picture("picture")
				.summonerName("summonerName")
				.build();
	}
}
