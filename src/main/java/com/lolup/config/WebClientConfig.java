package com.lolup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Bean
	public WebClient summonerWebClient() {
		return WebClient.builder()
				.baseUrl("https://kr.api.riotgames.com")
				.build();
	}

	@Bean
	public WebClient matchWebClient() {
		return WebClient.builder()
				.baseUrl("https://asia.api.riotgames.com")
				.build();
	}

	@Bean
	public WebClient riotStaticWebClient() {
		return WebClient.builder()
				.baseUrl("https://ddragon.leagueoflegends.com")
				.build();
	}

	@Bean
	public WebClient kakaoAuthorizationWebClient() {
		return WebClient.builder()
				.baseUrl("https://kauth.kakao.com")
				.build();
	}

	@Bean
	public WebClient kakaoResourceWebClient() {
		return WebClient.builder()
				.baseUrl("https://kapi.kakao.com")
				.build();
	}
}
