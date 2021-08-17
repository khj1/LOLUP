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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
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
@WebFluxTest(value = SummonerController.class, excludeAutoConfiguration = ReactiveSecurityAutoConfiguration.class)
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
    public void 소환사정보_정상조회_테스트() throws Exception {
        SummonerRankDto rankDto = SummonerRankDto.builder()
                .summonerName("name")
                .tier("BRONZE")
                .rank("3")
                .profileIconId(300)
                .wins(20)
                .losses(20)
                .build();

        Map<String, Integer> map = new HashMap<>();
        map.put("most1_championName", 3);
        map.put("most2_championName", 2);
        map.put("most3_championName", 2);

        SummonerDto Summonerdto = SummonerDto.builder()
                .info(rankDto)
                .latestWinRate("10%")
                .version("1")
                .most3(map)
                .build();

        when(service.find("correctName")).thenReturn(Summonerdto);

        webTestClient.get().uri("/riot/{summonerName}", "correctName")
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
                                fieldWithPath("version").description("게임 버전입니다. 원하는 이미지 resource를 API로 부터 호출하기 위해 필요합니다."),
                                fieldWithPath("latestWinRate").description("최근 10 게임에서의 승률 입니다."),
                                fieldWithPath("info.summonerName").description("인 게임에서 사용되는 소환사 이름입니다."),
                                fieldWithPath("info.tier").description("소환사의 랭크 티어입니다. 예) BRONZE"),
                                fieldWithPath("info.rank").description("소환사의 랭크 등급입니다. 예) ⅲ"),
                                fieldWithPath("info.profileIconId").description("인 게임에서 사용되는 소환사의 아이콘 이미지 식별 값입니다."),
                                fieldWithPath("info.wins").description("랭크 게임 전체 승리 횟수 입니다."),
                                fieldWithPath("info.losses").description("랭크 게임 전체 패배 횟수 입니다."),
                                fieldWithPath("most3.most1_championName").description("최근 10게임 내에서 가장 많이 플레이한 챔피언의 플레이 횟수 입니다."),
                                fieldWithPath("most3.most2_championName").description("최근 10게임 내에서 두번째로 많이 플레이한 챔피언의 플레이 횟수 입니다."),
                                fieldWithPath("most3.most3_championName").description("최근 10게임 내에서 세번째로 많이 플레이한 챔피언의 플레이 횟수 입니다.")
                        )
                ))
                .jsonPath("$.version").isEqualTo("1")
                .jsonPath("$.latestWinRate").isEqualTo("10%")
                .jsonPath("$.info.summonerName").isEqualTo("name")
                .jsonPath("$.info.tier").isEqualTo("BRONZE")
                .jsonPath("$.info.rank").isEqualTo("3")
                .jsonPath("$.info.profileIconId").isEqualTo(300)
                .jsonPath("$.info.wins").isEqualTo(20)
                .jsonPath("$.info.losses").isEqualTo(20)
                .jsonPath("$.most3").isNotEmpty();

    }

    @Test
    public void 잘못된_소환사_이름_조회() throws Exception {
        //when
        when(service.find("wrongName")).thenThrow(WebClientResponseException.class);

        //then
        webTestClient.get().uri("/riot/{summonerName}", "wrongName")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .consumeWith(document("summoner/exception",
                        preprocessResponse(prettyPrint())
                ))
                .jsonPath("$.code").isEqualTo("NOT FOUND")
                .jsonPath("$.message").isEqualTo("해당 소환사가 존재하지 않습니다.");

    }
}