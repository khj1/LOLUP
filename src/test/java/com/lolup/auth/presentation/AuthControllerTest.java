package com.lolup.auth.presentation;

import static com.lolup.common.fixture.AuthFixture.ACCESS_TOKEN;
import static com.lolup.common.fixture.AuthFixture.AUTHORIZATION_VALUE;
import static com.lolup.common.fixture.AuthFixture.REFRESH_TOKEN;
import static com.lolup.common.fixture.MemberFixture.MEMBER_ID;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.lolup.auth.application.dto.AccessTokenResponse;
import com.lolup.auth.application.dto.TokenResponse;
import com.lolup.auth.exception.GoogleAuthorizationException;
import com.lolup.auth.exception.GoogleResourceException;
import com.lolup.auth.exception.InvalidTokenException;
import com.lolup.auth.exception.KakaoAuthorizationException;
import com.lolup.auth.exception.KakaoResourceException;
import com.lolup.auth.exception.NoSuchRefreshTokenException;
import com.lolup.auth.presentation.dto.RefreshTokenDto;
import com.lolup.auth.presentation.dto.TokenRequest;
import com.lolup.common.ControllerTest;

class AuthControllerTest extends ControllerTest {

	private static final String DUMMY_ACCESS_TOKEN = "dummy.access.token";
	private static final String DUMMY_REFRESH_TOKEN = "dummy.refresh.token";

	@DisplayName("카카오 소셜 로그인을 정상적으로 수행하면 상태코드 200을 반환한다.")
	@Test
	void loginWithKakao() throws Exception {
		given(authService.createTokenWithKakaoOAuth(anyString(), anyString()))
				.willReturn(로그인_토큰_응답());

		mockMvc.perform(post("/auth/login/kakao")
						.header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_VALUE)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(로그인_토큰_요청()))
				)
				.andDo(document("auth/login/kakao",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestHeaders(
								headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 엑세스 토큰")
						),
						requestFields(
								fieldWithPath("code").description("카카오 인가 코드"),
								fieldWithPath("redirectUri").description("리다이렉트 URI")
						),
						responseFields(
								fieldWithPath("memberId").description("회원 ID"),
								fieldWithPath("accessToken").description("엑세스 토큰"),
								fieldWithPath("refreshToken").description("리프레시 토큰")
						)))
				.andExpect(status().isOk());
	}

	@DisplayName("전달받은 인가 코드나 redirectUri가 잘못된 경우 상태코드 401를 반환한다.")
	@Test
	void loginWithKakaoBadRequest() throws Exception {
		willThrow(new KakaoAuthorizationException())
				.given(authService)
				.createTokenWithKakaoOAuth(anyString(), anyString());

		mockMvc.perform(post("/auth/login/kakao")
						.header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_VALUE)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(로그인_토큰_요청()))
				)
				.andDo(document("auth/login/kakao/failByInvalidRequest",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestHeaders(
								headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 엑세스 토큰")
						),
						requestFields(
								fieldWithPath("code").description("카카오 인가 코드"),
								fieldWithPath("redirectUri").description("리다이렉트 URI")
						),
						responseFields(
								fieldWithPath("code").description("에러 코드"),
								fieldWithPath("message").description("에러 메세지")
						)))
				.andExpect(status().isBadRequest());
	}

	@DisplayName("카카오 서버에 문제가 있는 경우 상태코드 500을 반환한다.")
	@Test
	void loginWithKakaoServerError() throws Exception {
		willThrow(new KakaoResourceException())
				.given(authService)
				.createTokenWithKakaoOAuth(anyString(), anyString());

		mockMvc.perform(post("/auth/login/kakao")
						.header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_VALUE)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(로그인_토큰_요청()))
				)
				.andDo(document("auth/login/kakao/failByInternalServerError",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestHeaders(
								headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 엑세스 토큰")
						),
						requestFields(
								fieldWithPath("code").description("카카오 인가 코드"),
								fieldWithPath("redirectUri").description("리다이렉트 URI")
						),
						responseFields(
								fieldWithPath("code").description("에러 코드"),
								fieldWithPath("message").description("에러 메세지")
						)))
				.andExpect(status().isInternalServerError());
	}

	@DisplayName("인가 코드 또는 redirect URI가 공백인 경우 상태코드 401을 반환한다.")
	@ParameterizedTest
	@CsvSource({", redirectUri", "code,", ","})
	void loginWithKakaoEmptyRequest(final String code, final String redirectUri) throws Exception {
		mockMvc.perform(post("/auth/login/kakao")
						.header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_VALUE)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(new TokenRequest(code, redirectUri)))
				)
				.andDo(document("auth/login/kakao/failByEmptyRequest",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestHeaders(
								headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 엑세스 토큰")
						),
						requestFields(
								fieldWithPath("code").description("카카오 인가 코드"),
								fieldWithPath("redirectUri").description("리다이렉트 URI")
						),
						responseFields(
								fieldWithPath("code").description("에러 코드"),
								fieldWithPath("message").description("에러 메세지")
						)))
				.andExpect(status().isBadRequest());
	}

	@DisplayName("구글 소셜 로그인을 정상적으로 수행하면 상태코드 200을 반환한다.")
	@Test
	void loginWithGoogle() throws Exception {
		given(authService.createTokenWithGoogleOAuth(anyString(), anyString()))
				.willReturn(로그인_토큰_응답());

		mockMvc.perform(post("/auth/login/google")
						.header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_VALUE)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(로그인_토큰_요청()))
				)
				.andDo(document("auth/login/google",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestHeaders(
								headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 엑세스 토큰")
						),
						requestFields(
								fieldWithPath("code").description("구글 인가 코드"),
								fieldWithPath("redirectUri").description("리다이렉트 URI")
						),
						responseFields(
								fieldWithPath("memberId").description("회원 ID"),
								fieldWithPath("accessToken").description("엑세스 토큰"),
								fieldWithPath("refreshToken").description("리프레시 토큰")
						)))
				.andExpect(status().isOk());
	}

	@DisplayName("전달받은 인가 코드나 redirectUri가 잘못된 경우 상태코드 401를 반환한다.")
	@Test
	void loginWithGoogleBadReqeust() throws Exception {
		willThrow(new GoogleAuthorizationException())
				.given(authService)
				.createTokenWithGoogleOAuth(anyString(), anyString());

		mockMvc.perform(post("/auth/login/google")
						.header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_VALUE)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(로그인_토큰_요청()))
				)
				.andDo(document("auth/login/google/failByInvalidRequest",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestHeaders(
								headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 엑세스 토큰")
						),
						requestFields(
								fieldWithPath("code").description("구글 인가 코드"),
								fieldWithPath("redirectUri").description("리다이렉트 URI")
						),
						responseFields(
								fieldWithPath("code").description("에러 코드"),
								fieldWithPath("message").description("에러 메세지")
						)))
				.andExpect(status().isBadRequest());
	}

	@DisplayName("구글 서버에 문제가 있는 경우 상태코드 500을 반환한다.")
	@Test
	void loginWithGoogleServerError() throws Exception {
		willThrow(new GoogleResourceException())
				.given(authService)
				.createTokenWithGoogleOAuth(anyString(), anyString());

		mockMvc.perform(post("/auth/login/google")
						.header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_VALUE)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(로그인_토큰_요청()))
				)
				.andDo(document("auth/login/google/failByInternalServerError",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestHeaders(
								headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 엑세스 토큰")
						),
						requestFields(
								fieldWithPath("code").description("구글 인가 코드"),
								fieldWithPath("redirectUri").description("리다이렉트 URI")
						),
						responseFields(
								fieldWithPath("code").description("에러 코드"),
								fieldWithPath("message").description("에러 메세지")
						)))
				.andExpect(status().isInternalServerError());
	}

	@DisplayName("권한 체크가 완료되면 상태코드 204를 반환한다.")
	@Test
	void checkAuthorization() throws Exception {
		mockMvc.perform(get("/auth/check")
						.header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_VALUE)
				)
				.andDo(document("auth/logout",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestHeaders(
								headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 엑세스 토큰")
						)))
				.andExpect(status().isNoContent());
	}

	@DisplayName("리프레시 토큰으로 엑세스 토큰을 재발급 받으면 상태코드 200을 반환한다.")
	@Test
	void refreshToken() throws Exception {
		RefreshTokenDto 토큰_재발급_요청 = new RefreshTokenDto(DUMMY_REFRESH_TOKEN);
		AccessTokenResponse 토큰_재발급_응답 = new AccessTokenResponse(DUMMY_ACCESS_TOKEN);

		given(authService.refresh(anyString())).willReturn(토큰_재발급_응답);

		mockMvc.perform(post("/auth/refresh")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(토큰_재발급_요청))
				)
				.andDo(document("auth/refresh",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("refreshToken").description("리프레시 토큰")
						),
						responseFields(
								fieldWithPath("accessToken").description("재발급된 엑세스 토큰")
						)))
				.andExpect(status().isOk());
	}

	@DisplayName("잘못된 리프레시 토큰으로 엑세스 토큰을 재발급 받으면 상태코드 401을 반환한다.")
	@ParameterizedTest
	@ValueSource(classes = {InvalidTokenException.class, NoSuchRefreshTokenException.class})
	void refreshTokenWithInvalidToken(Class<? extends Throwable> expectedException) throws Exception {
		RefreshTokenDto 토큰_재발급_요청 = new RefreshTokenDto(DUMMY_REFRESH_TOKEN);
		AccessTokenResponse 토큰_재발급_응답 = new AccessTokenResponse(DUMMY_ACCESS_TOKEN);

		given(authService.refresh(anyString())).willThrow(expectedException);

		mockMvc.perform(post("/auth/refresh")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(토큰_재발급_요청))
				)
				.andDo(document("auth/refresh",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("refreshToken").description("리프레시 토큰")
						)))
				.andExpect(status().isUnauthorized());
	}

	@DisplayName("로그아웃을 하면 상태코드 204를 반환한다.")
	@Test
	void refreshTokenWithInvalidToken() throws Exception {
		RefreshTokenDto 로그아웃_요청 = new RefreshTokenDto(DUMMY_REFRESH_TOKEN);

		willDoNothing().given(authService).logout(anyString());

		mockMvc.perform(post("/auth/logout")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(로그아웃_요청))
				)
				.andDo(document("auth/logout",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("refreshToken").description("리프레시 토큰")
						)))
				.andExpect(status().isNoContent());
	}

	private TokenResponse 로그인_토큰_응답() {
		return new TokenResponse(Long.valueOf(MEMBER_ID), ACCESS_TOKEN, REFRESH_TOKEN);
	}

	private TokenRequest 로그인_토큰_요청() {
		return new TokenRequest("code", "redirect URI");
	}
}
