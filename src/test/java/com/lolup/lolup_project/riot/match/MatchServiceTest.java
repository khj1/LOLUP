package com.lolup.lolup_project.riot.match;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolup.lolup_project.riot.match.application.MatchService;
import com.lolup.lolup_project.riot.match.application.dto.MatchDto;
import com.lolup.lolup_project.riot.match.application.dto.MatchInfoDto;
import com.lolup.lolup_project.riot.match.application.dto.ParticipantDto;
import com.lolup.lolup_project.riot.match.application.dto.RecentMatchStatsDto;
import com.lolup.lolup_project.riot.match.exception.NoSuchSummonerException;
import com.lolup.lolup_project.riot.summoner.domain.MostInfo;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

//TODO
class MatchServiceTest {

	private static final String MOCK_SERVER_BASE_URL = "http://localhost:%s";
	private static final String SOLO_RANK_MATCH_ID_REQUEST_URI = "/lol/match/v5/matches/by-puuid/testPuuid/ids?queue=420&start=0&count=30&api_key=testApiKey";
	private static final String TEAM_RANK_MATCH_ID_REQUEST_URI = "/lol/match/v5/matches/by-puuid/testPuuid/ids?queue=440&start=0&count=30&api_key=testApiKey";

	private static final String PUUID = "testPuuid";
	private static final String TEST_API_KEY = "testApiKey";
	private static final String CHAMPION_NAME = "testChampionName";
	private static final String SUMMONER_NAME = "testSummonerName";
	private static final String OTHER_SUMMONER_NAME = "otherSummonerName";
	private static final String WRONG_SUMMONER_NAME = "wrongSummonerName";
	private static final int TOTAL_GAME_COUNT = 30;

	private static MockWebServer mockWebServer;
	private static MatchService matchService;
	private static ObjectMapper objectMapper;

	@BeforeAll
	static void setUp() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();

		String baseUrl = String.format(MOCK_SERVER_BASE_URL, mockWebServer.getPort());
		WebClient webClient = WebClient.create(baseUrl);
		matchService = new MatchService(webClient, TEST_API_KEY);

		objectMapper = new ObjectMapper();

		MockResponse 솔로_게임_ID_응답 = new MockResponse()
				.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.setBody(objectMapper.writeValueAsString(createMatchIds()));

		MockResponse 팀_게임_ID_응답 = new MockResponse()
				.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.setBody(objectMapper.writeValueAsString(createMatchIds()));

		MockResponse 게임_세부_정보_응답 = new MockResponse()
				.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.setBody(objectMapper.writeValueAsString(createMatchDto()));

		Dispatcher dispatcher = new Dispatcher() {
			@NotNull
			@Override
			public MockResponse dispatch(@NotNull final RecordedRequest request) {
				String path = Objects.requireNonNull(request.getPath());

				if (path.equals(SOLO_RANK_MATCH_ID_REQUEST_URI)) {
					return 솔로_게임_ID_응답;
				}
				if (path.equals(TEAM_RANK_MATCH_ID_REQUEST_URI)) {
					return 팀_게임_ID_응답;
				}
				return 게임_세부_정보_응답;
			}
		};

		mockWebServer.setDispatcher(dispatcher);
	}

	//TODO 통제하기 힘든 랜덤 값을 테스트에 활용하는 것은 좋아보이지 않는다.
	private static List<String> createMatchIds() {
		return Stream.generate(() -> UUID.randomUUID().toString())
				.limit(TOTAL_GAME_COUNT)
				.collect(Collectors.toList());
	}

	private static MatchDto createMatchDto() {
		return new MatchDto(createMatchInfoDto());
	}

	private static MatchInfoDto createMatchInfoDto() {
		return new MatchInfoDto(createParticipantDtos());
	}

	private static List<ParticipantDto> createParticipantDtos() {
		return List.of(createParticipantDto(SUMMONER_NAME), createParticipantDto(OTHER_SUMMONER_NAME));
	}

	private static ParticipantDto createParticipantDto(final String summonerName) {
		return ParticipantDto.builder()
				.summonerName(summonerName)
				.championName(CHAMPION_NAME)
				.championId(1)
				.teamId(1)
				.win(true)
				.build();
	}

	@AfterAll
	static void tearDown() throws IOException {
		mockWebServer.shutdown();
	}

	//TODO 현재 테스트는 너무 지엽적인 테스트, 보완할 필요가 있다.
	@DisplayName("최근 게임 통계를 불러온다.")
	@Test
	void getRecentMatchStats() {
		RecentMatchStatsDto recentMatchStats = matchService.getRecentMatchStats(SUMMONER_NAME, PUUID);

		double latestWinRate = recentMatchStats.getLatestWinRate();
		List<MostInfo> most3 = recentMatchStats.getMost3();

		assertAll(
				() -> assertThat(most3).hasSize(1),
				() -> assertThat(most3.get(0))
						.extracting("name", "play")
						.containsExactlyInAnyOrder(CHAMPION_NAME, 30L),
				() -> assertThat(latestWinRate).isEqualTo(1d)
		);
	}

	@DisplayName("존재하지 않는 소환사 이름을 입력하면 예외가 발생한다.")
	@Test
	void getRecentMatchStatsWithWrongSummonerName() {
		assertThatThrownBy(() -> matchService.getRecentMatchStats(WRONG_SUMMONER_NAME, PUUID))
				.isInstanceOf(NoSuchSummonerException.class);
	}
}
