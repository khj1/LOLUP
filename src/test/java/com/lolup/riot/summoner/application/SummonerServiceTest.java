package com.lolup.riot.summoner.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolup.duo.domain.SummonerRank;
import com.lolup.duo.domain.SummonerStat;
import com.lolup.duo.domain.SummonerTier;
import com.lolup.riot.match.exception.NoSuchSummonerException;
import com.lolup.riot.summoner.application.dto.SummonerAccountDto;
import com.lolup.riot.summoner.exception.RiotInternalServerError;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

class SummonerServiceTest {

	private static final String ID = "testId";
	private static final String NAME = "testName";
	private static final String PUUID = "testPuuid";
	private static final String ACCOUNT_ID = "testAccountId";
	private static final String SUMMONER_NAME = "testSummonerName";
	private static final String TEST_API_KEY = "testApiKey";
	private static final String WEB_CLIENT_BAD_REQUEST = "HTTP/1.1 404";
	private static final String WEB_CLIENT_BAD_RESPONSE = "HTTP/1.1 500";
	private static final String MOCK_SERVER_BASE_URL = "http://localhost:%s";
	private static final String ENCRYPTED_SUMMONER_ID = "testEncryptedSummonerId";
	private static final int PROFILE_ICON_ID = 1;
	private static final long REVISION_DATE = 1L;
	private static final long SUMMONER_LEVEL = 1L;

	private static MockWebServer mockWebServer;
	private static SummonerService summonerService;
	private static ObjectMapper objectMapper;

	@BeforeAll
	static void setUp() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();

		String baseUrl = String.format(MOCK_SERVER_BASE_URL, mockWebServer.getPort());
		WebClient webClient = WebClient.create(baseUrl);
		summonerService = new SummonerService(webClient, TEST_API_KEY);

		objectMapper = new ObjectMapper();
	}

	@AfterAll
	static void tearDown() throws IOException {
		mockWebServer.shutdown();
	}

	@DisplayName("라이엇 계정 정보를 불러온다.")
	@Test
	void getAccountInfo() throws JsonProcessingException {

		mockWebServer.enqueue(new MockResponse()
				.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.setBody(objectMapper.writeValueAsString(라이엇_계정_정보())));

		SummonerAccountDto API_호출_결과 = summonerService.requestAccountInfo(SUMMONER_NAME);

		assertThat(API_호출_결과)
				.usingRecursiveComparison()
				.isEqualTo(라이엇_계정_정보());
	}

	@DisplayName("계정 정보 호출 시 잘못된 소환사 이름을 입력하면 예외를 반환한다.")
	@Test
	void getAccountInfoWithWrongSummonerName() {
		mockWebServer.enqueue(new MockResponse()
				.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.setStatus(WEB_CLIENT_BAD_REQUEST));

		assertThatThrownBy(() -> summonerService.requestAccountInfo(SUMMONER_NAME))
				.isInstanceOf(NoSuchSummonerException.class);
	}

	@DisplayName("계정 정보 호출 시 라이엇 API 서버 내부에서 문제가 발생하면 예외를 반환한다.")
	@Test
	void getAccountInfoWithBadResponseFromRiotApi() {
		mockWebServer.enqueue(new MockResponse()
				.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.setStatus(WEB_CLIENT_BAD_RESPONSE));

		assertThatThrownBy(() -> summonerService.requestAccountInfo(SUMMONER_NAME))
				.isInstanceOf(RiotInternalServerError.class);
	}

	@DisplayName("소환사의 랭크 정보를 불러온다.")
	@Test
	void getSummonerStat() throws JsonProcessingException {
		mockWebServer.enqueue(new MockResponse()
				.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.setBody(objectMapper.writeValueAsString(소환사_통계())));

		SummonerStat API_호출_결과 = summonerService.requestSummonerStat(ENCRYPTED_SUMMONER_ID, SUMMONER_NAME);

		assertThat(API_호출_결과)
				.usingRecursiveComparison()
				.isEqualTo(소환사_통계());
	}

	@DisplayName("소환사가 아직 리그에 배치되지 않았다면 언랭크 상태의 초기 객체를 반환한다.")
	@Test
	void getUnrankedSummonerStat() {
		mockWebServer.enqueue(new MockResponse()
				.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE));

		SummonerStat API_호출_결과 = summonerService.requestSummonerStat(ENCRYPTED_SUMMONER_ID, SUMMONER_NAME);

		assertThat(API_호출_결과)
				.usingRecursiveComparison()
				.isEqualTo(언랭크_소환사_통계());

	}

	private SummonerAccountDto 라이엇_계정_정보() {
		return new SummonerAccountDto(ACCOUNT_ID, PROFILE_ICON_ID, REVISION_DATE, NAME, ID, PUUID, SUMMONER_LEVEL);
	}

	private SummonerStat 소환사_통계() {
		return new SummonerStat(SUMMONER_NAME, SummonerTier.CHALLENGER, SummonerRank.I, 100, 100);
	}

	private SummonerStat 언랭크_소환사_통계() {
		return new SummonerStat(SUMMONER_NAME, SummonerTier.UNRANKED, SummonerRank.UNRANKED, 0, 0);
	}
}
