package com.lolup.lolup_project.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lolup.lolup_project.auth.InvalidTokenException;

class JwtProviderTest {

	public static final String PAYLOAD = "payload";
	public static final String JWT_SECRET_KEY = "K".repeat(32); // JWT HMAC-SHA algorithm는 최소 32 바이트 이상
	public static final int ACCESS_TOKEN_VALIDITY_IN_MILLISECONDS = 3600000; // 1시간
	public static final int REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS = 1210000000; // 14일

	private final JwtProvider jwtProvider = new JwtProvider(
			JWT_SECRET_KEY,
			ACCESS_TOKEN_VALIDITY_IN_MILLISECONDS,
			REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS
	);

	@DisplayName("엑세스 토큰을 생성한다.")
	@Test
	void createAccessToken() {
		String accessToken = jwtProvider.createAccessToken(PAYLOAD);

		// jwt 토큰은 header, payload, signature로 구성된다.
		assertThat(accessToken.split("\\.")).hasSize(3);
	}

	@DisplayName("리프레쉬 토큰을 생성한다.")
	@Test
	void createRefreshToken() {
		String refreshToken = jwtProvider.createRefreshToken(PAYLOAD);

		assertThat(refreshToken.split("\\.")).hasSize(3);
	}

	@DisplayName("토큰이 만료되면 예외를 반환한다.")
	@Test
	void verifyExpiredToken() {
		JwtProvider expiredJwtProvider = new JwtProvider(JWT_SECRET_KEY, 0, 0);

		String expiredToken = expiredJwtProvider.createAccessToken(PAYLOAD);

		assertThatThrownBy(() -> expiredJwtProvider.verifyToken(expiredToken))
				.isInstanceOf(InvalidTokenException.class)
				.hasMessage("토큰 기한이 만료되었습니다.");
	}

	@DisplayName("유효하지 않은 토큰 형식을 입력받으면 예외를 반환한다.")
	@Test
	void verifyMalformedToken() {
		String malformedToken = "malformedToken";

		assertThatThrownBy(() -> jwtProvider.verifyToken(malformedToken))
				.isInstanceOf(InvalidTokenException.class)
				.hasMessage("유효하지 않은 토큰 형식입니다.");
	}

	@DisplayName("토큰의 시그니쳐가 일치하지 않으면 예외를 반환한다.")
	@Test
	void verifyInvalidSignatureToken() {
		JwtProvider otherJwtTokenProvider = new JwtProvider(
				"T".repeat(32),
				ACCESS_TOKEN_VALIDITY_IN_MILLISECONDS,
				REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS
		);

		String tokenWithOtherSignature = otherJwtTokenProvider.createAccessToken(PAYLOAD);

		assertThatThrownBy(() -> jwtProvider.verifyToken(tokenWithOtherSignature))
				.isInstanceOf(InvalidTokenException.class)
				.hasMessage("권한이 없습니다.");
	}

	@DisplayName("토큰의 Payload를 가져온다.")
	@Test
	void getTokenClaims() {
		String token = jwtProvider.createAccessToken(PAYLOAD);

		String payload = jwtProvider.getTokenClaims(token);

		assertThat(payload).isEqualTo(PAYLOAD);
	}
}
