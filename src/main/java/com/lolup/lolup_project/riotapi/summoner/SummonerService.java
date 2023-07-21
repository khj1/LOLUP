package com.lolup.lolup_project.riotapi.summoner;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class SummonerService {

	private static final String ACCOUNT_INFO_REQUEST_URI = "/lol/summoner/v4/summoners/by-name/{summonerName}?api_key={apiKey}";
	private static final String RANK_INFO_REQUEST_URI = "/lol/league/v4/entries/by-summoner/{encryptedSummonerId}?api_key={apiKey}";
	private static final int INITIAL_WINS = 0;
	private static final int INITIAL_LOSSES = 0;

	private final WebClient webClient;
	private final String apiKey;

	public SummonerService(@Qualifier("summonerWebClient") final WebClient webClient,
						   @Value("${security.riot.api-key}") final String apiKey) {
		this.webClient = webClient;
		this.apiKey = apiKey;
	}

	public SummonerAccountDto getAccountInfo(final String summonerName) {
		return webClient
				.get()
				.uri(ACCOUNT_INFO_REQUEST_URI, summonerName, apiKey)
				.retrieve()
				.bodyToMono(SummonerAccountDto.class)
				.block();
	}

	public SummonerRankInfo getSummonerTotalSoloRankInfo(final String encryptedSummonerId, final String summonerName) {
		return createSummonerRankInfo(encryptedSummonerId, summonerName);
	}

	private SummonerRankInfo createSummonerRankInfo(final String encryptedSummonerId, final String summonerName) {
		return webClient
				.get()
				.uri(RANK_INFO_REQUEST_URI, encryptedSummonerId, apiKey)
				.retrieve()
				.bodyToMono(SummonerRankInfo.class)
				.blockOptional()
				.orElse(createUnrankedInfo(summonerName));
	}

	private SummonerRankInfo createUnrankedInfo(final String summonerName) {
		return SummonerRankInfo.builder()
				.summonerName(summonerName)
				.tier(SummonerTier.UNRANKED)
				.rank("언랭크")
				.wins(INITIAL_WINS)
				.losses(INITIAL_LOSSES)
				.build();
	}
}
