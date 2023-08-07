package com.lolup.riot.match.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolup.duo.application.dto.ChampionStatDto;
import com.lolup.riot.match.application.dto.MatchDto;
import com.lolup.riot.match.application.dto.ParticipantDto;
import com.lolup.riot.match.application.dto.RecentMatchStatsDto;
import com.lolup.riot.match.exception.InvalidPuuIdException;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

//TODO
class MatchServiceTest {

	private static final String PUUID = "testPuuid";
	private static final String OTHER_PUUID = "otherPuuid";
	private static final String INVALID_PUUID = "invalidPuuid";
	private static final String TEST_API_KEY = "testApiKey";
	private static final String CHAMPION_NAME = "testChampionName";
	private static final String MOCK_SERVER_BASE_URL = "http://localhost:%s";
	private static final String SOLO_RANK_MATCH_ID_REQUEST_URI = "/lol/match/v5/matches/by-puuid/testPuuid/ids?queue=420&start=0&count=10&api_key=testApiKey";
	private static final String MATCH_REQUEST_URI = "/lol/match/v5/matches/%s?api_key=testApiKey";

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

		MockResponse 매치_ID_응답 = new MockResponse()
				.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.setBody(objectMapper.writeValueAsString(매치_ID_목록()));

		MockResponse A_승리_매치_응답 = 매치_정보_응답("ChampionA", true);
		MockResponse A_패배_매치_응답 = 매치_정보_응답("ChampionA", false);
		MockResponse B_승리_매치_응답 = 매치_정보_응답("ChampionB", true);
		MockResponse B_패배_매치_응답 = 매치_정보_응답("ChampionB", false);
		MockResponse C_승리_매치_응답 = 매치_정보_응답("ChampionC", true);
		MockResponse C_패배_매치_응답 = 매치_정보_응답("ChampionC", false);

		Dispatcher dispatcher = new Dispatcher() {
			@NotNull
			@Override
			public MockResponse dispatch(@NotNull final RecordedRequest request) {
				String path = Objects.requireNonNull(request.getPath());

				if (path.equals(SOLO_RANK_MATCH_ID_REQUEST_URI)) {
					return 매치_ID_응답;
				}
				if (path.equals(String.format(MATCH_REQUEST_URI, "ChampionAWin"))) {
					return A_승리_매치_응답;
				}
				if (path.equals(String.format(MATCH_REQUEST_URI, "ChampionALose"))) {
					return A_패배_매치_응답;
				}
				if (path.equals(String.format(MATCH_REQUEST_URI, "ChampionBWin"))) {
					return B_승리_매치_응답;
				}
				if (path.equals(String.format(MATCH_REQUEST_URI, "ChampionBLose"))) {
					return B_패배_매치_응답;
				}
				if (path.equals(String.format(MATCH_REQUEST_URI, "ChampionCWin"))) {
					return C_승리_매치_응답;
				}
				if (path.equals(String.format(MATCH_REQUEST_URI, "ChampionCLose"))) {
					return C_패배_매치_응답;
				}
				return new MockResponse().setResponseCode(404);
			}
		};

		mockWebServer.setDispatcher(dispatcher);
	}

	private static MockResponse 매치_정보_응답(final String championName, final boolean win) throws JsonProcessingException {
		return new MockResponse()
				.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.setBody(objectMapper.writeValueAsString(매치_정보(championName, win)));
	}

	@AfterAll
	static void tearDown() throws IOException {
		mockWebServer.shutdown();
	}

	private static List<String> 매치_ID_목록() {
		return List.of(
				"ChampionAWin", "ChampionAWin", "ChampionAWin", "ChampionAWin", "ChampionALose",
				"ChampionBWin", "ChampionBWin", "ChampionBLose", "ChampionCLose", "ChampionCLose"
		);
	}

	private static MatchDto 매치_정보(final String championName, final boolean win) {
		return new MatchDto(createMatchInfoDto(championName, win));
	}

	private static MatchDto.MatchInfoDto createMatchInfoDto(final String championName, final boolean win) {
		return new MatchDto.MatchInfoDto(createParticipantDtos(championName, win));
	}

	private static List<ParticipantDto> createParticipantDtos(final String championName, final boolean win) {
		return List.of(
				createParticipantDto(PUUID, championName, win),
				createParticipantDto(OTHER_PUUID)
		);
	}

	private static ParticipantDto createParticipantDto(final String puuid) {
		return new ParticipantDto(puuid, CHAMPION_NAME, CHAMPION_ID, TEAM_ID, WIN);
	}

	private static ParticipantDto createParticipantDto(final String puuid, final String championName,
													   final boolean win) {
		return new ParticipantDto(puuid, championName, CHAMPION_ID, TEAM_ID, win);
	}

	@DisplayName("최근 게임 통계를 불러온다.")
	@Test
	void getRecentMatchStats() {
		RecentMatchStatsDto recentMatchStats = matchService.requestRecentMatchStats(PUUID);

		double latestWinRate = recentMatchStats.getLatestWinRate();
		List<ChampionStatDto> championStats = recentMatchStats.getChampionStats();

		assertAll(
				() -> assertThat(latestWinRate).isEqualTo(0.6d),
				() -> assertThat(championStats).hasSize(3),
				() -> assertThat(championStats)
						.extracting("name", "count")
						.containsExactly(
								tuple("ChampionA", 5L),
								tuple("ChampionB", 3L),
								tuple("ChampionC", 2L)
						)
		);
	}

	@DisplayName("유효하지 않은 Puuid를 입력하면 예외가 발생한다.")
	@Test
	void getRecentMatchStatsWithWrongSummonerName() {
		assertThatThrownBy(() -> matchService.requestRecentMatchStats(INVALID_PUUID))
				.isInstanceOf(InvalidPuuIdException.class);
	}
}
