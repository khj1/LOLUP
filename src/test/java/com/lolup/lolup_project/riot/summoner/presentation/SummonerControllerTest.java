package com.lolup.lolup_project.riot.summoner.presentation;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolup.lolup_project.riot.summoner.application.SummonerService;
import com.lolup.lolup_project.riot.summoner.application.dto.SummonerAccountDto;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@WithMockUser
@WebFluxTest(value = SummonerController.class)
class SummonerControllerTest {

	@MockBean
	SummonerService service;
	private WebTestClient webTestClient;

	@BeforeEach
	public void setUp(ApplicationContext applicationContext, RestDocumentationContextProvider restDocumentation) {
		webTestClient = WebTestClient.bindToApplicationContext(applicationContext)
				.configureClient()
				.baseUrl("http://lolup-api.p-e.kr/")
				.filter(documentationConfiguration(restDocumentation))
				.build();
	}

	@Test
	public void 소환사_정상조회_테스트() {
		SummonerAccountDto dto = SummonerAccountDto.builder()
				.accountId("accountId")
				.profileIconId(100)
				.id("id")
				.puuid("puuid")
				.summonerLevel(100L)
				.name("summonerName")
				.build();

		when(service.requestAccountInfo("summonerName")).thenReturn(dto);

		webTestClient.get().uri("/riot/find/{summonerName}", "summonerName")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.consumeWith(document("summoner/success",
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("summonerName").description("인 게임에서 사용하는 소환사 이름을 전달합니다.")
						),
						responseFields(
								fieldWithPath("summonerName").type("String").description("API에 저장된 다듬어진 소환사 이름을 반환합니다.")
						)
				))
				.jsonPath("$.summonerName").isEqualTo("summonerName");

	}

	@Test
	public void 잘못된_소환사_이름_조회() throws Exception {
		//when
		when(service.requestAccountInfo("wrongName")).thenThrow(WebClientResponseException.class);

		//then
		webTestClient.get().uri("/riot/find/{summonerName}", "wrongName")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectBody()
				.consumeWith(document("summoner/exception",
						preprocessResponse(prettyPrint())
				))
				.jsonPath("$.code").isEqualTo(404)
				.jsonPath("$.message").isEqualTo("해당 소환사가 존재하지 않습니다.");

	}
}
