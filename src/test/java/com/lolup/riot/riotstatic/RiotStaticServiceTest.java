package com.lolup.riot.riotstatic;

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

class RiotStaticServiceTest {

	private static final String MOCK_SERVER_BASE_URL = "http://localhost:%s";
	private static final String LATEST_GAME_VERSION = "1.4";

	private static MockWebServer mockWebServer;
	private static RiotStaticService riotStaticService;
	private static ObjectMapper objectMapper;

	@BeforeAll
	static void setUp() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();

		String baseUrl = String.format(MOCK_SERVER_BASE_URL, mockWebServer.getPort());
		WebClient webClient = WebClient.create(baseUrl);
		riotStaticService = new RiotStaticService(webClient);

		objectMapper = new ObjectMapper();
	}

	@AfterAll
	static void tearDown() throws IOException {
		mockWebServer.shutdown();
	}

	@DisplayName("현재 최신 게임 버전을 불러온다.")
	@Test
	void getLatestGameVersion() throws JsonProcessingException {
		String[] 게임_버전_응답 = {LATEST_GAME_VERSION, "1.3", "1.2", "1.1"};

		mockWebServer.enqueue(new MockResponse()
				.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.setBody(objectMapper.writeValueAsString(게임_버전_응답)));

		String 최신_게임_버전 = riotStaticService.getLatestGameVersion();

		assertThat(최신_게임_버전).isEqualTo(LATEST_GAME_VERSION);
	}
}
