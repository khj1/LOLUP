package com.lolup.lolup_project.auth;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

	private final static String BEARER_JWT_TOKEN = "Bearer provided.jwt.token";
	private static final String DUMMY_REFRESH_TOKEN = "dummy.refresh.token";
	private static final String DUMMY_ACCESS_TOKEN = "dummy.access.token";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private AuthService authService;

	@BeforeEach
	void setUp(
			final WebApplicationContext context,
			final RestDocumentationContextProvider provider
	) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(MockMvcRestDocumentation.documentationConfiguration(provider))
				.build();
	}

	@DisplayName("권한 체크가 완료되면 상태코드 204를 반환한다.")
	@Test
	void checkAuthorization() throws Exception {
		mockMvc.perform(get("/auth/check")
						.header(HttpHeaders.AUTHORIZATION, BEARER_JWT_TOKEN)
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

		given(authService.refreshToken(anyString())).willReturn(토큰_재발급_응답);

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

		given(authService.refreshToken(anyString())).willThrow(expectedException);

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
}
