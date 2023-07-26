package com.lolup.member.presentation;

import static org.mockito.ArgumentMatchers.any;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.lolup.common.ControllerTest;
import com.lolup.member.exception.NoSuchMemberException;
import com.lolup.member.presentation.dto.MemberUpdateRequest;

class MemberControllerTest extends ControllerTest {

	private final static String BEARER_JWT_TOKEN = "Bearer provided.jwt.token";

	@DisplayName("소환사 이름을 수정하면 상태코드 204를 반환한다.")
	@Test
	void updateSummonerName() throws Exception {
		willDoNothing()
				.given(memberService)
				.updateSummonerName(any(), any());

		MemberUpdateRequest 회원_수정_요청 = new MemberUpdateRequest("changedName");

		mockMvc.perform(patch("/member")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, BEARER_JWT_TOKEN)
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

	@DisplayName("소환사 이름을 수정할 때 잘못된 멤버 ID를 입력하면 상태코드 404를 반환한다.")
	@Test
	void updateSummonerNameWithInvalidMemberId() throws Exception {
		willThrow(new NoSuchMemberException())
				.given(memberService)
				.updateSummonerName(any(), any());

		MemberUpdateRequest 회원_수정_요청 = new MemberUpdateRequest("changedName");

		mockMvc.perform(patch("/member")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, BEARER_JWT_TOKEN)
						.content(objectMapper.writeValueAsString(회원_수정_요청))
				)
				.andDo(print())
				.andDo(document("member/update/failByNoMember",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestHeaders(
								headerWithName("Authorization").description("JWT 엑세스 토큰")
						),
						requestFields(
								fieldWithPath("summonerName").description("수정할 소환사 이름")
						)))
				.andExpect(status().isNotFound());
	}
}
