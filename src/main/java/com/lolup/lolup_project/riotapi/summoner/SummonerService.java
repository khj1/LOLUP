package com.lolup.lolup_project.riotapi.summoner;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class SummonerService {

	private static final String ACCOUNT_INFO_REQUEST_URI = "/lol/summoner/v4/summoners/by-name/{summonerName}?api_key={apiKey}";
	private static final String RANK_INFO_REQUEST_URI = "/lol/league/v4/entries/by-summoner/{encryptedSummonerId}?api_key={apiKey}";

	private final WebClient webClient;
	private final String apiKey;

	public SummonerService(@Qualifier("summonerWebClient") final WebClient webClient,
						   @Value("${security.riot.api-key}") final String apiKey) {
		this.webClient = webClient;
		this.apiKey = apiKey;
	}

	public SummonerRankInfo getSummonerTotalSoloRankInfo(String summonerName) {
		SummonerAccountDto summonerAccountInfo = getAccountInfo(summonerName);
		String id = summonerAccountInfo.getId();
		int iconId = summonerAccountInfo.getProfileIconId();

		SummonerRankInfoDto summonerRankInfoDto = getRankInfoDto(id);

		if (summonerRankInfoDto == null) {
			return getUnrankedInfo(summonerName);
		}

		return getRankInfo(summonerRankInfoDto, iconId);
	}

	public SummonerAccountDto getAccountInfo(String summonerName) {
		return webClient
				.get()
				.uri(ACCOUNT_INFO_REQUEST_URI, summonerName, apiKey)
				.retrieve()
				.bodyToMono(SummonerAccountDto.class)
				.block();
	}

	public SummonerRankInfoDto getRankInfoDto(String encryptedSummonerId) {
		return webClient
				.get()
				.uri(RANK_INFO_REQUEST_URI, encryptedSummonerId, apiKey)
				.retrieve()
				.bodyToFlux(SummonerRankInfoDto.class)
				.blockFirst();
	}

	private SummonerRankInfo getUnrankedInfo(String summonerName) {
		return SummonerRankInfo.builder()
				.summonerName(summonerName)
				.tier("UNRANKED")
				.rank("언랭크")
				.wins(0)
				.losses(0)
				.build();
	}

	private SummonerRankInfo getRankInfo(SummonerRankInfoDto summonerRankInfoDto, int iconId) {
		return SummonerRankInfo.builder()
				.summonerName(summonerRankInfoDto.getSummonerName())
				.tier(summonerRankInfoDto.getTier())
				.rank(summonerRankInfoDto.getRank())
				.wins(summonerRankInfoDto.getWins())
				.losses(summonerRankInfoDto.getLosses())
				.iconId(iconId)
				.build();
	}
}
