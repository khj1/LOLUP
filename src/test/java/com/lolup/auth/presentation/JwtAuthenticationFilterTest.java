package com.lolup.auth.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class JwtAuthenticationFilterTest {

	private static final String BEARER = "Bearer ";
	private static final String AUTHORIZED_PATH = "/duo";
	private static final String WRONG_TOKEN = "wrong.jwt.token";

	@Autowired
	private MockMvc mockMvc;

	@DisplayName("헤더가 비어있다면 예외를 반환한다.")
	@Test
	void filterTestWithEmptyHeader() throws Exception {
		mockMvc.perform(get(AUTHORIZED_PATH))
				.andExpect(status().isUnauthorized());
	}

	@DisplayName("헤더에 잘못된 토큰이 포함되어 있으면 예외를 반환한다.")
	@Test
	void filterTestWithWrongToken() throws Exception {
		mockMvc.perform(get(AUTHORIZED_PATH)
						.header(HttpHeaders.AUTHORIZATION, BEARER + WRONG_TOKEN)
				)
				.andExpect(status().isUnauthorized());
	}
}
