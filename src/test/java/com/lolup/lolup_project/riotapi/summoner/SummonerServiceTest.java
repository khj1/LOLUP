package com.lolup.lolup_project.riotapi.summoner;

import static org.assertj.core.api.Assertions.assertThat;

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

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

class SummonerServiceTest {

	private static final String MOCK_SERVER_BASE_URL = "http://localhost:%s";
	private static final String TEST_API_KEY = "testApiKey";

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
		SummonerAccountDto 라이엇_계정_정보_응답 = createSummonerAccountDto();

		mockWebServer.enqueue(new MockResponse()
				.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.setBody(objectMapper.writeValueAsString(라이엇_계정_정보_응답)));

		SummonerAccountDto API_호출_결과 = summonerService.getAccountInfo("testName");

		assertThat(API_호출_결과)
				.usingRecursiveComparison()
				.isEqualTo(라이엇_계정_정보_응답);
	}

	private SummonerAccountDto createSummonerAccountDto() {
		return SummonerAccountDto.builder()
				.id("id")
				.name("testName")
				.puuId("puuId")
				.accountId("accountId")
				.revisionDate(1L)
				.summonerLevel(1L)
				.profileIconId(1)
				.build();
	}
}
