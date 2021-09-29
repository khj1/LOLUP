package com.lolup.lolup_project.duo;

import com.lolup.lolup_project.api.riot_api.summoner.MostInfo;
import com.lolup.lolup_project.api.riot_api.summoner.SummonerPosition;
import com.lolup.lolup_project.api.riot_api.summoner.SummonerTier;
import com.lolup.lolup_project.member.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
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
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@WithMockUser
@WebFluxTest(value = DuoController.class)
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
        when(duoService.save(duoForm)).thenReturn(duoForm.getMemberId());
        
        //then
        webTestClient.mutateWith(csrf()).post().uri("duo/new")
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
        Map<String, Object> map = getDuoMap();


        //when
        when(duoService.findAll(SummonerPosition.ALL, SummonerTier.ALL, 1)).thenReturn(map);

        //then
        webTestClient.get().uri(uriBuilder ->
                        uriBuilder
                                .path("/duo")
                                .queryParam("position", SummonerPosition.ALL)
                                .queryParam("tier", SummonerTier.ALL)
                                .queryParam("section", 1)
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Map.class)
                .consumeWith(document("duo/readAll",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("position").description("주 포지션. 조회 항목을 필터링하기 위해 사용합니다."),
                                parameterWithName("tier").description("게임 티어. 조회 항목을 필터링하기 위해 사용합니다."),
                                parameterWithName("section").description("페이징 처리에 사용되는 페이징 구간을 의미합니다. 한 섹션당 20개의 데이터가 추가로 호출됩니다.")
                        ),
                        responseFields(
                                fieldWithPath("totalCount").description("DB에 저장된 총 듀오 데이터 수"),
                                fieldWithPath("version").description("현재 게임 버전"),
                                subsectionWithPath("data").type("List<DuoDto>").description("페이징 처리된 듀오 리스트")
                        ),
                        responseFields(beneathPath("data"),
                                fieldWithPath("duoId").type("Long").description("작성한 모집글의 고유 번호"),
                                fieldWithPath("memberId").type("Long").description("작성자의 회원 고유 번호"),
                                fieldWithPath("iconId").type("int").description("프로필 아이콘 번호"),
                                fieldWithPath("summonerName").description("인 게임에서 사용되는 소환사 이름"),
                                fieldWithPath("position").description("주 포지션"),
                                fieldWithPath("tier").description("게임 티어"),
                                fieldWithPath("rank").description("티어 등급"),
                                subsectionWithPath("most3").type("List<MostInfo>").description("최근 10 게임에서 가장 많이 플레이한 챔피언들"),
                                fieldWithPath("wins").type("int").description("총 승리 횟수"),
                                fieldWithPath("losses").type("int").description("총 패배 횟수"),
                                fieldWithPath("latestWinRate").description("최근 10 게임의 승률"),
                                fieldWithPath("desc").description("신청자 모집을 위해 간단한 문구를 작성할 수 있습니다."),
                                fieldWithPath("postDate").type("LocalDateTime").description("모집글 작성 시간")
                        )
                ));

    }

    private Map<String, Object> getDuoMap() {
        DuoDto duoDto1 = getDuoDto();
        DuoDto duoDto2 = getDuoDto();
        List<DuoDto> data = new ArrayList<>();
        data.add(duoDto1);
        data.add(duoDto2);

        Map<String, Object> map = new HashMap<>();
        map.put("version", "11.16.0");
        map.put("totalCount", 100);
        map.put("data", data);

        return map;
    }

    private DuoDto getDuoDto() {
        return DuoDto.builder()
                .iconId(100)
                .duoId(2L)
                .latestWinRate("20%")
                .losses(300)
                .most3(getMost3())
                .rank("IV")
                .tier(SummonerTier.BRONZE)
                .desc("hi")
                .wins(400)
                .memberId(1L)
                .postDate(LocalDateTime.now())
                .summonerName("hideonbush")
                .position(SummonerPosition.MID)
                .build();
    }

    private List<MostInfo> getMost3() {
        List<MostInfo> most3 = new ArrayList<>();

        most3.add(MostInfo.create("Syndra", 4));
        most3.add(MostInfo.create("Lucian", 3));
        most3.add(MostInfo.create("Zed", 2));

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
                                subsectionWithPath("most3").type("List<MostInfo>").description("최근 10 게임에서 가장 많이 플레이한 챔피언들"),
                                fieldWithPath("wins").type("int").description("총 승리 횟수"),
                                fieldWithPath("losses").type("int").description("총 패배 횟수"),
                                fieldWithPath("latestWinRate").description("최근 10 게임의 승률"),
                                fieldWithPath("desc").description("신청자 모집을 위해 간단한 문구를 작성할 수 있습니다."),
                                fieldWithPath("postDate").type("LocalDateTime").description("모집글 작성 시간")
                        ),
                        responseFields(beneathPath("most3"),
                                fieldWithPath("name").description("챔피언의 이름."),
                                fieldWithPath("play").description("판수.")
                        )
                ));

    }

    @Test
    void 모집글_삭제() {
        //given
        Long duoId = 1L;
        Long memberId = 1L;

        //when
        when(duoService.delete(duoId, memberId)).thenReturn(1L);

        //then
        webTestClient.mutateWith(csrf()).delete().uri(uriBuilder ->
                    uriBuilder
                            .path("duo/{duoId}")
                            .queryParam("memberId", memberId)
                            .build(duoId)
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(Long.class)
                .consumeWith(document("duo/delete",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("memberId").description("로그인한 본인의 memberId를 전달합니다. duoId에 해당하는 memberId와 불일치하면 글이 삭제되지 않습니다.")
                        )
                ));
    }
}