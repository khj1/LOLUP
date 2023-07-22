package com.lolup.lolup_project.auth.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lolup.lolup_project.auth.domain.RefreshTokenRepository;
import com.lolup.lolup_project.auth.dto.AccessTokenResponse;
import com.lolup.lolup_project.auth.exception.NoSuchRefreshTokenException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public AccessTokenResponse refreshToken(final String refreshToken) {
		verifyRefreshToken(refreshToken);

		String memberId = jwtTokenProvider.getPayload(refreshToken);
		String accessToken = jwtTokenProvider.createAccessToken(memberId);

		return new AccessTokenResponse(accessToken);
	}

	private void verifyRefreshToken(final String refreshToken) {
		refreshTokenRepository.findByRefreshToken(refreshToken)
				.orElseThrow(NoSuchRefreshTokenException::new);

		jwtTokenProvider.verifyToken(refreshToken);
	}

	@Transactional
	public void logout(final String refreshToken) {
		refreshTokenRepository.deleteByRefreshToken(refreshToken);
	}
}
