package com.lolup.lolup_project.duo;

import com.lolup.lolup_project.api.riot_api.summoner.SummonerPosition;
import com.lolup.lolup_project.api.riot_api.summoner.SummonerTier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@WebFluxTest(value = DuoController.class, excludeAutoConfiguration = ReactiveSecurityAutoConfiguration.class)
class DuoControllerTest {

    private WebTestClient webTestClient;

    @MockBean
    private DuoService duoService;

    @BeforeEach
    public void setUp(ApplicationContext applicationContext, RestDocumentationContextProvider restDocumentation) {
        this.webTestClient = WebTestClient.bindToApplicationContext(applicationContext)
                .configureClient()
                .baseUrl("http://lolup-api.p-e.kr/")
                .filter(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void 데이터_추가() {
        //given
        DuoForm duoForm = getDuoForm();
        
        //when
        when(duoService.save(duoForm)).thenReturn(1L);
        
        //then
        webTestClient.post().uri("duo/new")
                .body(Mono.just(duoForm), DuoForm.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Long.class)
                .consumeWith(document("duo/create",
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("summonerName").description("인 게임에서 사용되는 소환사 이름"),
                                fieldWithPath("memberId").type("Long").description("작성자의 회원 고유 번호"),
                                fieldWithPath("position").description("주 포지션"),
                                fieldWithPath("desc").description("신청자 모집을 위해 간단한 문구를 작성할 수 있습니다."),
                                fieldWithPath("postDate").type("LocalDateTime").description("모집글 작성 시간")
                        )
                ));
    }


    private DuoForm getDuoForm() {
        return DuoForm.builder()
                .position(SummonerPosition.MID)
                .summonerName("hideonbush")
                .desc("hi")
                .postDate(LocalDateTime.now())
                .memberId(1L)
                .build();
    }

    @Test
    void 모집글_모두_조회() {
        //given
        List<DuoDto> duoList = getDuoList();

        //when
        when(duoService.findAll(SummonerPosition.ALL, SummonerTier.ALL)).thenReturn(duoList);

        //then
        webTestClient.get().uri(uriBuilder ->
                        uriBuilder
                                .path("/duo")
                                .queryParam("position", SummonerPosition.ALL)
                                .queryParam("tier", SummonerTier.ALL)
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DuoDto.class).hasSize(2)
                .consumeWith(document("duo/readAll",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("position").description("주 포지션. 조회 항목을 필터링하기 위해 사용합니다."),
                                parameterWithName("tier").description("게임 티어. 조회 항목을 필터링하기 위해 사용합니다.")
                        )
                ));

    }

    private List<DuoDto> getDuoList() {
        DuoDto duoDto1 = getDuoDto();
        DuoDto duoDto2 = getDuoDto();
        List<DuoDto> list = new ArrayList<>();
        list.add(duoDto1);
        list.add(duoDto2);

        return list;
    }

    private DuoDto getDuoDto() {
        return DuoDto.builder()
                .iconId(100)
                .duoId(2L)
                .latestWinRate("20%")
                .lose(300)
                .most3(getMost3())
                .rank("IV")
                .tier(SummonerTier.BRONZE)
                .desc("hi")
                .win(400)
                .memberId(1L)
                .postDate(LocalDateTime.now())
                .summonerName("hideonbush")
                .position(SummonerPosition.MID)
                .build();
    }

    private Map<String, Integer> getMost3() {
        Map<String, Integer> most3 = new HashMap<>();
        most3.put("most1_champion_name", 3);
        most3.put("most2_champion_name", 2);
        most3.put("most3_champion_name", 1);
        return most3;
    }

    @Test
    void 모집글_하나만_조회() {
        //given
        DuoDto duoDto = getDuoDto();

        //when
        when(duoService.findById(1L)).thenReturn(duoDto);

        //then
        webTestClient.get().uri("duo/{duoId}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("duo/readOne",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("duoId").type("Long").description("작성한 모집글의 고유 번호"),
                                fieldWithPath("memberId").type("Long").description("작성자의 회원 고유 번호"),
                                fieldWithPath("iconId").type("int").description("프로필 아이콘 번호"),
                                fieldWithPath("summonerName").description("인 게임에서 사용되는 소환사 이름"),
                                fieldWithPath("position").description("주 포지션"),
                                fieldWithPath("tier").description("게임 티어"),
                                fieldWithPath("rank").description("티어 등급"),
                                subsectionWithPath("most3").type("Map<String, Integer>").description("최근 10 게임에서 가장 많이 플레이한 챔피언들"),
                                fieldWithPath("win").type("int").description("총 승리 횟수"),
                                fieldWithPath("lose").type("int").description("총 패배 횟수"),
                                fieldWithPath("latestWinRate").description("최근 10 게임의 승률"),
                                fieldWithPath("desc").description("신청자 모집을 위해 간단한 문구를 작성할 수 있습니다."),
                                fieldWithPath("postDate").type("LocalDateTime").description("모집글 작성 시간")
                        ),
                        responseFields(beneathPath("most3"),
                                fieldWithPath("most1_champion_name").description("최근 10 게임에서 첫번째로 많이 플레이한 챔피언. 챔피언의 이름이 key값, 판수가 value값으로 지정된다."),
                                fieldWithPath("most2_champion_name").description("최근 10 게임에서 두번째로 많이 플레이한 챔피언. 챔피언의 이름이 key값, 판수가 value값으로 지정된다."),
                                fieldWithPath("most3_champion_name").description("최근 10 게임에서 세번째로 많이 플레이한 챔피언. 챔피언의 이름이 key값, 판수가 value값으로 지정된다.")
                        )
                ));

    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}