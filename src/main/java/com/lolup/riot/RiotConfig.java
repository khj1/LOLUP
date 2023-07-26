package com.lolup.riot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RiotConfig {

	@Bean
	public WebClient summonerWebClient() {
		return WebClient
				.builder()
				.baseUrl("https://kr.api.riotgames.com")
				.build();
	}

	@Bean
	public WebClient matchWebClient() {
		return WebClient
				.builder()
				.baseUrl("https://asia.api.riotgames.com")
				.build();
	}

	@Bean
	public WebClient riotStaticWebClient() {
		return WebClient
				.builder()
				.baseUrl("https://ddragon.leagueoflegends.com")
				.build();
	}
}
