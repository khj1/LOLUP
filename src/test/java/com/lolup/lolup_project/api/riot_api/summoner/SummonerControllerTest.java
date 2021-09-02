package com.lolup.lolup_project.api.riot_api.summoner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.reactive.server.WebTestClientBuilderCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@WithMockUser
@WebFluxTest(value = SummonerController.class)
class SummonerControllerTest {

    private WebTestClient webTestClient;

    @MockBean
    SummonerService service;

    @BeforeEach
    public void setUp(ApplicationContext applicationContext, RestDocumentationContextProvider restDocumentation){
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext)
                .configureClient()
                .baseUrl("http://lolup-api.p-e.kr/")
                .filter(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    public void 소환사_정상조회_테스트() throws Exception {

        SummonerAccountDto dto = SummonerAccountDto.builder()
                .accountId("accountId")
                .iconId(100)
                .id("id")
                .puuid("puuid")
                .summonerLevel(100L)
                .name("summonerName")
                .build();


        when(service.getAccountInfo("summonerName")).thenReturn(dto);

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
        when(service.getAccountInfo("wrongName")).thenThrow(WebClientResponseException.class);

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