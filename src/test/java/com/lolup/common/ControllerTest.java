package com.lolup.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolup.auth.application.AuthService;
import com.lolup.auth.application.JwtTokenProvider;
import com.lolup.auth.presentation.AuthController;
import com.lolup.duo.application.DuoService;
import com.lolup.duo.presentation.DuoController;
import com.lolup.member.application.MemberService;
import com.lolup.member.presentation.MemberController;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest({
		AuthController.class,
		DuoController.class,
		MemberController.class
})
@ActiveProfiles("test")
public class ControllerTest {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@MockBean
	protected JwtTokenProvider jwtTokenProvider;

	@MockBean
	protected AuthService authService;

	@MockBean
	protected DuoService duoService;

	@MockBean
	protected MemberService memberService;

	@BeforeEach
	void setUp(
			final WebApplicationContext context,
			final RestDocumentationContextProvider provider
	) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(MockMvcRestDocumentation.documentationConfiguration(provider))
				.build();
	}
}
