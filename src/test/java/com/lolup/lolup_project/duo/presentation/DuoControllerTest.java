package com.lolup.lolup_project.duo.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolup.lolup_project.auth.application.JwtTokenProvider;
import com.lolup.lolup_project.duo.application.DuoService;
import com.lolup.lolup_project.duo.application.dto.ChampionStatDto;
import com.lolup.lolup_project.duo.application.dto.DuoDto;
import com.lolup.lolup_project.duo.application.dto.DuoResponse;
import com.lolup.lolup_project.duo.application.dto.DuoSaveRequest;
import com.lolup.lolup_project.duo.domain.SummonerPosition;
import com.lolup.lolup_project.duo.domain.SummonerRank;
import com.lolup.lolup_project.duo.domain.SummonerTier;
import com.lolup.lolup_project.duo.exception.DuoDeleteFailureException;
import com.lolup.lolup_project.duo.exception.NoSuchDuoException;
import com.lolup.lolup_project.duo.presentation.dto.DuoUpdateRequest;
import com.lolup.lolup_project.member.exception.NoSuchMemberException;
import com.lolup.lolup_project.riot.summoner.domain.ChampionStat;

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

	@DisplayName("모집글 추가에 성공하면 상태코드 201을 반환한다.")
	@Test
	void save() throws Exception {
		DuoSaveRequest 듀오_생성_요청 = createDuoSaveRequest();

		willDoNothing()
				.given(duoService)
				.save(anyLong(), any(DuoSaveRequest.class));

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
								fieldWithPath("desc").description("신청자 모집을 위해 간단한 문구를 작성할 수 있습니다.")
						)))
				.andExpect(status().isCreated());
	}

	@DisplayName("유효하지 않은 멤버 ID로 듀오 모집글을 생성하면 상태코드 404를 반환한다.")
	@Test
	void saveWithInvalidMemberId() throws Exception {
		DuoSaveRequest 듀오_생성_요청 = createDuoSaveRequest();

		willThrow(new NoSuchMemberException())
				.given(duoService)
				.save(anyLong(), any(DuoSaveRequest.class));

		mockMvc.perform(post("/duo/new/failByNoMember")
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
								fieldWithPath("desc").description("신청자 모집을 위해 간단한 문구를 작성할 수 있습니다.")
						)))
				.andExpect(status().isNotFound());

	}

	@DisplayName("모집글 조회에 성공하면 상태코드 200을 반환한다.")
	@Test
	void findAll() throws Exception {
		DuoResponse 모집글_조회_응답 = createDuoResponse();

		given(duoService.findAll(any(), any(), any()))
				.willReturn(모집글_조회_응답);

		mockMvc.perform(get("/duo")
						.queryParam("position", SummonerPosition.MID.name())
						.queryParam("tier", SummonerTier.PLATINUM.name())
						.queryParam("page", "0")
						.queryParam("size", "20")
				)
				.andDo(document("duo/findAll",
						preprocessRequest(prettyPrint()),
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
								subsectionWithPath("content").type("List<DuoDto>").description("페이징 처리된 듀오 리스트"),
								subsectionWithPath("pageable").description("페이징 처리와 관련된 데이터")
						),
						responseFields(beneathPath("content"),
								fieldWithPath("duoId").type("Long").description("작성한 모집글의 고유 번호"),
								fieldWithPath("memberId").type("Long").description("작성자의 회원 고유 번호"),
								fieldWithPath("iconId").type("int").description("프로필 아이콘 번호"),
								fieldWithPath("summonerName").description("인 게임에서 사용되는 소환사 이름"),
								fieldWithPath("position").description("주 포지션"),
								fieldWithPath("tier").description("게임 티어"),
								fieldWithPath("rank").description("티어 등급"),
								subsectionWithPath("championStats").type("List<ChampionStatsDtos>")
										.description("최근 10 게임에서 가장 많이 플레이한 챔피언들"),
								fieldWithPath("wins").type("int").description("총 승리 횟수"),
								fieldWithPath("losses").type("int").description("총 패배 횟수"),
								fieldWithPath("latestWinRate").description("최근 10 게임의 승률"),
								fieldWithPath("desc").description("신청자 모집을 위해 간단한 문구를 작성할 수 있습니다."),
								fieldWithPath("postDate").type("LocalDateTime").description("모집글 작성 시간"))))
				.andExpect(status().isOk());
	}

	@DisplayName("모집글을 수정에 성공하면 상태코드 204를 반환한다.")
	@Test
	void update() throws Exception {
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

	@DisplayName("잘못된 멤버 ID로 모집글을 수정하면 시도하면 상태코드 404를 반환한다.")
	@Test
	void updateWithInvalidDuoId() throws Exception {
		willThrow(new NoSuchDuoException())
				.given(duoService)
				.update(anyLong(), any(), any());

		DuoUpdateRequest 듀오_수정_요청 = new DuoUpdateRequest(SummonerPosition.MID, "description");

		mockMvc.perform(patch("/duo/{duoId}", 1L)
						.header(HttpHeaders.AUTHORIZATION, BEARER_JWT_TOKEN)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(듀오_수정_요청))
				)
				.andDo(document("duo/update/failByNoDuo",
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
				.andExpect(status().isNotFound());
	}

	@DisplayName("모집글 제거에 성공하면 상태코드 204를 반환한다.")
	@Test
	void deleteDuo() throws Exception {
		Long duoId = 1L;
		willDoNothing()
				.given(duoService)
				.delete(anyLong(), anyLong());

		mockMvc.perform(delete("/duo/{duoId}", duoId)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, BEARER_JWT_TOKEN)
				)
				.andDo(document("duo/delete",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestHeaders(
								headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 엑세스 토큰")
						),
						pathParameters(
								parameterWithName("duoId").description("모집글 식별자")
						)))
				.andExpect(status().isNoContent());
	}

	@DisplayName("모집글 제거 시 잘못된 멤버 ID 또는 듀오 ID를 입력하면 상태코드 404를 반환한다.")
	@Test
	void deleteDuoWithInvalidID() throws Exception {
		Long duoId = 1L;
		willThrow(new DuoDeleteFailureException())
				.given(duoService)
				.delete(any(), any());

		mockMvc.perform(delete("/duo/{duoId}", duoId)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, BEARER_JWT_TOKEN)
				)
				.andDo(document("duo/delete/failByInvalidId",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestHeaders(
								headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 엑세스 토큰")
						),
						pathParameters(
								parameterWithName("duoId").description("모집글 식별자")
						)))
				.andExpect(status().isNotFound());
	}

	private DuoSaveRequest createDuoSaveRequest() {
		return DuoSaveRequest.builder()
				.position(SummonerPosition.MID)
				.summonerName("hideonbush")
				.desc("hi")
				.build();
	}

	private DuoResponse createDuoResponse() {
		DuoDto duoDtoA = getDuoDto(1L);
		DuoDto duoDtoB = getDuoDto(2L);

		List<DuoDto> content = new ArrayList<>();
		content.add(duoDtoA);
		content.add(duoDtoB);

		String version = "11.16.0";
		long totalCount = 100L;
		Pageable pageable = PageRequest.of(0, 20);

		return new DuoResponse(version, totalCount, content, pageable);
	}

	private DuoDto getDuoDto(final Long duoId) {
		return DuoDto.builder()
				.iconId(100)
				.duoId(duoId)
				.latestWinRate(0.2d)
				.losses(300)
				.championStats(getChampionStats().stream().map(ChampionStatDto::create).collect(Collectors.toList()))
				.rank(SummonerRank.IV)
				.tier(SummonerTier.BRONZE)
				.desc("hi")
				.wins(400)
				.memberId(1L)
				.summonerName("hideonbush")
				.position(SummonerPosition.MID)
				.build();
	}

	private List<ChampionStat> getChampionStats() {
		List<ChampionStat> championStats = new ArrayList<>();

		championStats.add(ChampionStat.create("Syndra", 4L));
		championStats.add(ChampionStat.create("Lucian", 3L));
		championStats.add(ChampionStat.create("Zed", 2L));

		return championStats;
	}
}
