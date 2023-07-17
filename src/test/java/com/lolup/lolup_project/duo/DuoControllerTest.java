package com.lolup.lolup_project.duo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolup.lolup_project.riotapi.summoner.MostInfo;
import com.lolup.lolup_project.riotapi.summoner.MostInfoDto;
import com.lolup.lolup_project.riotapi.summoner.SummonerPosition;
import com.lolup.lolup_project.riotapi.summoner.SummonerTier;
import com.lolup.lolup_project.token.JwtTokenProvider;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(DuoController.class)
class DuoControllerTest {

	private final static String BEARER_JWT_TOKEN = "Bearer provided.jwt.token";

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	DuoService duoService;

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

	@Test
	void 데이터_추가() throws Exception {
		DuoSaveRequest 듀오_생성_요청 = createDuoSaveRequest();

		willDoNothing()
				.given(duoService)
				.save(anyLong(), any());

		mockMvc.perform(post("/duo/new")
						.header(HttpHeaders.AUTHORIZATION, BEARER_JWT_TOKEN)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(듀오_생성_요청))
				)
				.andDo(document("duo/create",
						preprocessResponse(prettyPrint()),
						requestHeaders(
								headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 엑세스 토큰")
						),
						requestFields(
								fieldWithPath("summonerName").description("인 게임에서 사용되는 소환사 이름"),
								fieldWithPath("position").description("주 포지션"),
								fieldWithPath("desc").description("신청자 모집을 위해 간단한 문구를 작성할 수 있습니다."),
								fieldWithPath("postDate").type("LocalDateTime").description("모집글 작성 시간")
						)))
				.andExpect(status().isCreated());
	}

	private DuoSaveRequest createDuoSaveRequest() {
		return DuoSaveRequest.builder()
				.position(SummonerPosition.MID)
				.summonerName("hideonbush")
				.desc("hi")
				.postDate(LocalDateTime.now())
				.build();
	}

	@Test
	void 모집글_모두_조회() throws Exception {
		//given
		Map<String, Object> map = getDuoMap();

		//when
		when(duoService.findAll(any(), any(), any())).thenReturn(map);

		ResultActions result = mockMvc.perform(
				get("/duo")
						.queryParam("position", Position.MID.toString())
						.queryParam("tier", SummonerTier.PLATINUM)
						.queryParam("page", "0")
						.queryParam("size", "20")
						.accept(MediaType.APPLICATION_JSON)
		);

		//then
		result.andExpect(status().isOk())
				.andDo(document("duo/readAll",
						preprocessResponse(prettyPrint()),
						queryParameters(
								parameterWithName("position").description("주 포지션. 조회 항목을 필터링하기 위해 사용합니다.").optional(),
								parameterWithName("tier").description("게임 티어. 조회 항목을 필터링하기 위해 사용합니다.").optional(),
								parameterWithName("page").description("현재 페이지"),
								parameterWithName("size").description("한 페이지당 조회할 데이터 개수(생략하면 default 값, 20)")
						),
						responseFields(
								fieldWithPath("totalCount").description("DB에 저장된 총 듀오 데이터 수"),
								fieldWithPath("version").description("현재 게임 버전"),
								subsectionWithPath("data").type("List<DuoDto>").description("페이징 처리된 듀오 리스트"),
								subsectionWithPath("pageable").description("페이징 처리와 관련된 데이터")
						),
						responseFields(beneathPath("data"),
								fieldWithPath("duoId").type("Long").description("작성한 모집글의 고유 번호"),
								fieldWithPath("memberId").type("Long").description("작성자의 회원 고유 번호"),
								fieldWithPath("iconId").type("int").description("프로필 아이콘 번호"),
								fieldWithPath("summonerName").description("인 게임에서 사용되는 소환사 이름"),
								fieldWithPath("position").description("주 포지션"),
								fieldWithPath("tier").description("게임 티어"),
								fieldWithPath("rank").description("티어 등급"),
								subsectionWithPath("most3").type("List<MostInfo>")
										.description("최근 10 게임에서 가장 많이 플레이한 챔피언들"),
								fieldWithPath("wins").type("int").description("총 승리 횟수"),
								fieldWithPath("losses").type("int").description("총 패배 횟수"),
								fieldWithPath("latestWinRate").description("최근 10 게임의 승률"),
								fieldWithPath("desc").description("신청자 모집을 위해 간단한 문구를 작성할 수 있습니다."),
								fieldWithPath("postDate").type("LocalDateTime").description("모집글 작성 시간")
						)
						//                        responseFields(beneathPath("pageable"))
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
		map.put("pageable", PageRequest.of(0, 20));

		return map;
	}

	private DuoDto getDuoDto() {
		return DuoDto.builder()
				.iconId(100)
				.duoId(2L)
				.latestWinRate("20%")
				.losses(300)
				.most3(getMost3().stream().map(MostInfoDto::create).collect(Collectors.toList()))
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

	@DisplayName("모집글을 수정 기능")
	@Test
	void updateDuo() throws Exception {
		willDoNothing()
				.given(duoService)
				.update(anyLong(), any(), any());

		DuoUpdateRequest 듀오_수정_요청 = new DuoUpdateRequest(SummonerPosition.MID, "description");

		mockMvc.perform(patch("/duo/{duoId}", 1L)
						.header(HttpHeaders.AUTHORIZATION, BEARER_JWT_TOKEN)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(듀오_수정_요청))
				)
				.andDo(document("duo/update",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestHeaders(
								headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 엑세스 토큰")
						),
						pathParameters(
								parameterWithName("duoId").description("모집글 식별자")
						),
						requestFields(
								fieldWithPath("position").description("주 포지션"),
								fieldWithPath("desc").description("신청자 모집을 위한 간단한 문구")
						)))
				.andExpect(status().isNoContent());
	}

	@Test
	void 모집글_삭제() throws Exception {
		//given
		Long duoId = 1L;
		Long memberId = 1L;

		//when
		when(duoService.delete(anyLong(), anyLong())).thenReturn(duoId);

		ResultActions result = mockMvc.perform(
				delete("/duo/{duoId}", duoId)
						.header(HttpHeaders.AUTHORIZATION, BEARER_JWT_TOKEN)
						.queryParam("memberId", String.valueOf(memberId))
						.accept(MediaType.APPLICATION_JSON)
		);

		//then
		result.andExpect(status().isOk())
				.andDo(document("duo/delete",
						preprocessResponse(prettyPrint()),
						requestHeaders(
								headerWithName(HttpHeaders.AUTHORIZATION).description("유저 식별 토큰(Bearer)")
						),
						queryParameters(
								parameterWithName("memberId").description(
										"로그인한 본인의 memberId를 전달합니다. duoId에 해당하는 memberId와 불일치하면 글이 삭제되지 않습니다.")
						)
				));
	}
}
