package com.lolup.lolup_project.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;

import com.lolup.lolup_project.auth.application.AuthorizationExtractor;
import com.lolup.lolup_project.auth.exception.EmptyAuthorizationHeaderException;
import com.lolup.lolup_project.auth.exception.InvalidTokenException;

class AuthorizationExtractorTest {

	public static final String BEARER_TYPE = "Bearer ";
	public static final String TOKEN_VALUE = "provided.jwt.token";

	@DisplayName("Authorization Header에 Beraer 토큰이 있는 경우 토큰 정상 반환")
	@Test
	void extract() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(HttpHeaders.AUTHORIZATION, BEARER_TYPE + TOKEN_VALUE);

		String token = AuthorizationExtractor.extract(request);

		assertThat(token).isEqualTo(TOKEN_VALUE);
	}

	@DisplayName("Authorization Header가 비어있는 경우 예외 발생")
	@Test
	void extractEmptyAuthorizationHeader() {
		MockHttpServletRequest emptyAuthorizationHeaderRequest = new MockHttpServletRequest();

		assertThatThrownBy(() -> AuthorizationExtractor.extract(emptyAuthorizationHeaderRequest))
				.isInstanceOf(EmptyAuthorizationHeaderException.class)
				.hasMessage("Authorization header가 존재하지 않습니다.");
	}

	@DisplayName("Bearer 타입이 없는 Authorization header의 경우 예외 발생")
	@Test
	void extractInvalidAuthorizationHeader() {
		MockHttpServletRequest invalidAuthorizationHeaderRequest = new MockHttpServletRequest();
		invalidAuthorizationHeaderRequest.addHeader(HttpHeaders.AUTHORIZATION, TOKEN_VALUE);

		assertThatThrownBy(() -> AuthorizationExtractor.extract(invalidAuthorizationHeaderRequest))
				.isInstanceOf(InvalidTokenException.class)
				.hasMessage("부적절한 token 형식입니다.");
	}
}
