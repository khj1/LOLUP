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
    public void 소환사정보_정상조회_테스트() throws Exception {
        SummonerRankDto rankDto = SummonerRankDto.builder()
                .summonerName("name")
                .tier("BRONZE")
                .rank("3")
                .iconId(300)
                .win(20)
                .lose(20)
                .build();

        List<MostInfo> most3 = new ArrayList<>();

        most3.add(MostInfo.create("Syndra", 4));
        most3.add(MostInfo.create("Lucian", 3));
        most3.add(MostInfo.create("Zed", 2));

        SummonerDto Summonerdto = SummonerDto.builder()
                .info(rankDto)
                .latestWinRate("10%")
                .version("1")
                .most3(most3)
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
                                fieldWithPath("version").type("String").description("게임 버전입니다. 원하는 이미지 resource를 API로 부터 호출하기 위해 필요합니다."),
                                fieldWithPath("latestWinRate").type("String").description("최근 10 게임에서의 승률 입니다."),
                                subsectionWithPath("info").type("SummonerRankDto").description("소환사의 전반적인 정보를 담고 있습니다."),
                                subsectionWithPath("most3").type("List<MostInfo>").description("최근 10게임 내에서 가장 많이 플레이한 3 챔피언들의 정보입니다.")
                        ),
                        responseFields(beneathPath("info"),
                                fieldWithPath("summonerName").description("인 게임에서 사용되는 소환사 이름입니다."),
                                fieldWithPath("tier").description("소환사의 랭크 티어입니다. 예) BRONZE"),
                                fieldWithPath("rank").description("소환사의 랭크 등급입니다. 예) ⅲ"),
                                fieldWithPath("iconId").description("인 게임에서 사용되는 소환사의 아이콘 이미지 식별 값입니다."),
                                fieldWithPath("win").description("랭크 게임 전체 승리 횟수 입니다."),
                                fieldWithPath("lose").description("랭크 게임 전체 패배 횟수 입니다.")
                        ),
                        responseFields(beneathPath("most3"),
                                fieldWithPath("name").description("챔피언의 이름."),
                                fieldWithPath("play").type("int").description("판수.")
                        )
                ))
                .jsonPath("$.version").isEqualTo("1")
                .jsonPath("$.latestWinRate").isEqualTo("10%")
                .jsonPath("$.info.summonerName").isEqualTo("name")
                .jsonPath("$.info.tier").isEqualTo("BRONZE")
                .jsonPath("$.info.rank").isEqualTo("3")
                .jsonPath("$.info.iconId").isEqualTo(300)
                .jsonPath("$.info.win").isEqualTo(20)
                .jsonPath("$.info.lose").isEqualTo(20)
                .jsonPath("$.most3[0].name").isEqualTo("Syndra")
                .jsonPath("$.most3[0].play").isEqualTo(4);

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