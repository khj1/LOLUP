package com.lolup.riot.match.application;

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
import com.lolup.riot.match.application.dto.MatchDto;
import com.lolup.riot.match.application.dto.ParticipantDto;
import com.lolup.riot.match.application.dto.RecentMatchStatsDto;
import com.lolup.riot.match.exception.NoSuchSummonerException;
import com.lolup.riot.summoner.domain.ChampionStat;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

//TODO
class MatchServiceTest {

	private static final String PUUID = "testPuuid";
	private static final String TEST_API_KEY = "testApiKey";
	private static final String SUMMONER_NAME = "testSummonerName";
	private static final String CHAMPION_NAME = "testChampionName";
	private static final String INVALID_SUMMONER_NAME = "invalidSummonerName";
	private static final String OTHER_SUMMONER_NAME = "otherSummonerName";
	private static final String MOCK_SERVER_BASE_URL = "http://localhost:%s";
	private static final String SOLO_RANK_MATCH_ID_REQUEST_URI = "/lol/match/v5/matches/by-puuid/testPuuid/ids?queue=420&start=0&count=30&api_key=testApiKey";
	private static final String TEAM_RANK_MATCH_ID_REQUEST_URI = "/lol/match/v5/matches/by-puuid/testPuuid/ids?queue=440&start=0&count=30&api_key=testApiKey";

	private static final int TOTAL_GAME_COUNT = 30;
	private static final int CHAMPION_ID = 1;
	private static final int TEAM_ID = 1;
	private static final boolean WIN = true;

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

		MockResponse 솔로_매치_ID_응답 = new MockResponse()
				.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.setBody(objectMapper.writeValueAsString(테스트_매치_ID_목록()));

		MockResponse 팀_매치_ID_응답 = new MockResponse()
				.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.setBody(objectMapper.writeValueAsString(테스트_매치_ID_목록()));

		MockResponse 매치_정보_응답 = new MockResponse()
				.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.setBody(objectMapper.writeValueAsString(매치_정보()));

		Dispatcher dispatcher = new Dispatcher() {
			@NotNull
			@Override
			public MockResponse dispatch(@NotNull final RecordedRequest request) {
				String path = Objects.requireNonNull(request.getPath());

				if (path.equals(SOLO_RANK_MATCH_ID_REQUEST_URI)) {
					return 솔로_매치_ID_응답;
				}
				if (path.equals(TEAM_RANK_MATCH_ID_REQUEST_URI)) {
					return 팀_매치_ID_응답;
				}
				return 매치_정보_응답;
			}
		};

		mockWebServer.setDispatcher(dispatcher);
	}

	@AfterAll
	static void tearDown() throws IOException {
		mockWebServer.shutdown();
	}

	//TODO 통제하기 힘든 랜덤 값을 테스트에 활용하는 것은 좋아보이지 않는다.
	private static List<String> 테스트_매치_ID_목록() {
		return Stream.generate(() -> UUID.randomUUID().toString())
				.limit(TOTAL_GAME_COUNT)
				.collect(Collectors.toList());
	}

	private static MatchDto 매치_정보() {
		return new MatchDto(createMatchInfoDto());
	}

	private static MatchDto.MatchInfoDto createMatchInfoDto() {
		return new MatchDto.MatchInfoDto(createParticipantDtos());
	}

	private static List<ParticipantDto> createParticipantDtos() {
		return List.of(
				createParticipantDto(SUMMONER_NAME),
				createParticipantDto(OTHER_SUMMONER_NAME)
		);
	}

	private static ParticipantDto createParticipantDto(final String summonerName) {
		return new ParticipantDto(summonerName, CHAMPION_NAME, CHAMPION_ID, TEAM_ID, WIN);
	}

	//TODO 현재 테스트는 너무 지엽적인 테스트, 보완할 필요가 있다.
	@DisplayName("최근 게임 통계를 불러온다.")
	@Test
	void getRecentMatchStats() {
		RecentMatchStatsDto recentMatchStats = matchService.requestRecentMatchStats(SUMMONER_NAME, PUUID);

		double latestWinRate = recentMatchStats.getLatestWinRate();
		List<ChampionStat> championStats = recentMatchStats.getChampionStats();

		assertAll(
				() -> assertThat(latestWinRate).isEqualTo(1d),
				() -> assertThat(championStats).hasSize(1),
				() -> assertThat(championStats.get(0))
						.extracting("name", "count")
						.containsExactlyInAnyOrder(CHAMPION_NAME, 30L)
		);
	}

	@DisplayName("존재하지 않는 소환사 이름을 입력하면 예외가 발생한다.")
	@Test
	void getRecentMatchStatsWithWrongSummonerName() {
		assertThatThrownBy(() -> matchService.requestRecentMatchStats(INVALID_SUMMONER_NAME, PUUID))
				.isInstanceOf(NoSuchSummonerException.class);
	}
}
