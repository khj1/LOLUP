package com.lolup.lolup_project.member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolup.lolup_project.member.application.MemberService;
import com.lolup.lolup_project.member.dto.MemberUpdateRequest;
import com.lolup.lolup_project.member.presentation.MemberController;
import com.lolup.lolup_project.token.JwtTokenProvider;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(MemberController.class)
class MemberControllerTest {

	private final static String BEARER = "Bearer ";
	private final static String JWT_TOKEN = "provided.jwt.token";

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	MemberService memberService;

	@MockBean
	JwtTokenProvider jwtTokenProvider;

	@BeforeEach
	void setUp(
			final WebApplicationContext context,
			final RestDocumentationContextProvider provider
	) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(documentationConfiguration(provider))
				.build();
	}

	@DisplayName("소환사 이름을 수정한다.")
	@Test
	void updateSummonerName() throws Exception {
		willDoNothing()
				.given(memberService)
				.updateSummonerName(any(), any());

		MemberUpdateRequest 회원_수정_요청 = new MemberUpdateRequest("changedName");

		mockMvc.perform(patch("/member")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, BEARER + JWT_TOKEN)
						.content(objectMapper.writeValueAsString(회원_수정_요청))
				)
				.andDo(print())
				.andDo(document("member/update",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestHeaders(
								headerWithName("Authorization").description("JWT 엑세스 토큰")
						),
						requestFields(
								fieldWithPath("summonerName").description("수정할 소환사 이름")
						)))
				.andExpect(status().isNoContent());
	}
}
