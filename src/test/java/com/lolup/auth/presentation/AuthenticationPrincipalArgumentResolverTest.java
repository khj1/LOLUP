package com.lolup.auth.presentation;

import static com.lolup.common.fixture.MemberFixture.신규_회원;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import com.lolup.common.ServiceTest;

@AutoConfigureMockMvc
class AuthenticationPrincipalArgumentResolverTest extends ServiceTest {

	private static final String BEARER = "Bearer ";
	private static final String AUTHORIZED_PATH = "/auth/check";
	private static final String WRONG_TOKEN = "wrong.jwt.token";

	@Autowired
	private MockMvc mockMvc;

	@DisplayName("전달된 토큰에서 회원 ID를 정상적으로 파싱한다.")
	@Test
	void resolveArgument() throws Exception {
		Long memberId = memberRepository.save(신규_회원()).getId();
		String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(memberId));

		mockMvc.perform(get(AUTHORIZED_PATH)
						.header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
				)
				.andExpect(status().isNoContent());
	}

	@DisplayName("헤더가 비어있다면 예외를 반환한다.")
	@Test
	void resolveArgumentWithEmptyHeader() throws Exception {
		mockMvc.perform(get(AUTHORIZED_PATH))
				.andExpect(status().isUnauthorized());
	}

	@DisplayName("헤더에 잘못된 토큰이 포함되어 있으면 예외를 반환한다.")
	@Test
	void resolveArgumentWithWrongToken() throws Exception {
		mockMvc.perform(get(AUTHORIZED_PATH)
						.header(HttpHeaders.AUTHORIZATION, BEARER + WRONG_TOKEN)
				)
				.andExpect(status().isUnauthorized());
	}
}
