package com.lolup.riot.riotstatic;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.lolup.riot.summoner.exception.NoGameVersionException;

@Service
public class RiotStaticService {

	private static final String GAME_VERSION_REQUEST_URI = "/api/versions.json";
	private static final int LATEST_VERSION_INDEX = 0;

	private final WebClient webClient;

	public RiotStaticService(@Qualifier("riotStaticWebClient") final WebClient webClient) {
		this.webClient = webClient;
	}

	public String getLatestGameVersion() {
		String[] versions = webClient
				.get()
				.uri(GAME_VERSION_REQUEST_URI)
				.retrieve()
				.bodyToMono(String[].class)
				.blockOptional()
				.orElseThrow(NoGameVersionException::new);

		return versions[LATEST_VERSION_INDEX];
	}
}
