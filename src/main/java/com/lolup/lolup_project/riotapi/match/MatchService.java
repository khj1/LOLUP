package com.lolup.lolup_project.riotapi.match;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.lolup.lolup_project.riotapi.summoner.MostInfo;
import com.lolup.lolup_project.riotapi.summoner.SummonerService;

@Service
public class MatchService {

	private static final String MATCH_ID_REQUEST_URI = "/lol/match/v5/matches/by-puuid/{puuId}/ids?api_key={apiKey}&start=0&count=30";
	private static final String MATCH_REQUEST_URI = "/lol/match/v5/matches/{matchId}?api_key={apiKey}";

	private final WebClient webClient;
	private final String apiKey;
	private final SummonerService summonerService;

	public MatchService(@Qualifier("matchWebClient") final WebClient webClient,
						@Value("${security.riot.api-key}") final String apiKey,
						final SummonerService summonerService) {
		this.webClient = webClient;
		this.apiKey = apiKey;
		this.summonerService = summonerService;
	}

	public RecentMatchStatsDto getRecentMatchStats(final String summonerName) {
		List<MatchInfoDto> matchInfoDtos = getMatchInfos(summonerName);
		String latestWinRate = getLatestWinRate(summonerName, matchInfoDtos);
		List<MostInfo> most3 = getLatestMost3(summonerName, matchInfoDtos);

		return new RecentMatchStatsDto(latestWinRate, most3);
	}

	public List<MatchInfoDto> getMatchInfos(String summonerName) {

		List<MatchInfoDto> matchInfoDtos = new ArrayList<>();
		String[] matchIds = getMatchIds(summonerName);

		Arrays.stream(matchIds).forEach(matchId -> matchInfoDtos.add(getMatchDto(matchId).getInfo()));

		return matchInfoDtos;
	}

	private String[] getMatchIds(String summonerName) {
		String puuId = summonerService.getAccountInfo(summonerName).getPuuId();
		return webClient
				.get()
				.uri(MATCH_ID_REQUEST_URI, puuId, apiKey)
				.retrieve()
				.bodyToMono(String[].class)
				.block();
	}

	private MatchDto getMatchDto(String matchId) {
		return webClient
				.get()
				.uri(MATCH_REQUEST_URI, matchId, apiKey)
				.retrieve()
				.bodyToMono(MatchDto.class)
				.block();
	}

	private String getLatestWinRate(String summonerName, List<MatchInfoDto> matchInfoDtos) {
		long winCount = matchInfoDtos.stream()
				.filter(matchInfo -> getWin(summonerName, matchInfo)).count();

		return ((double)winCount / 30) * 100 + "%";
	}

	private Boolean getWin(String summonerName, MatchInfoDto matchInfoDto) {
		return matchInfoDto
				.getParticipants()
				.stream().filter(participantDto -> participantDto.getSummonerName().equals(summonerName))
				.collect(Collectors.toList())
				.get(0)
				.isWin();
	}

	private List<MostInfo> getLatestMost3(String summonerName, List<MatchInfoDto> matches) {
		Map<String, Integer> mostsIn30Games = getMostsIn30Games(summonerName, matches);
		List<Map.Entry<String, Integer>> sortedMosts = getSortedMosts(mostsIn30Games);
		List<Map.Entry<String, Integer>> most3Entries = getMost3Entries(sortedMosts);

		return getMost3List(most3Entries);
	}

	private List<MostInfo> getMost3List(List<Map.Entry<String, Integer>> entries) {
		List<MostInfo> mostInfos = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : entries) {
			MostInfo mostInfo = MostInfo.create(entry.getKey(), entry.getValue());
			mostInfos.add(mostInfo);
		}
		return mostInfos;
	}

	private List<Map.Entry<String, Integer>> getMost3Entries(List<Map.Entry<String, Integer>> sortedMosts) {
		if (sortedMosts.size() >= 3) {
			return sortedMosts.subList(0, 3);
		}
		if (sortedMosts.size() == 2) {
			return sortedMosts.subList(0, 2);
		}
		return sortedMosts.subList(0, 1);
	}

	private List<Map.Entry<String, Integer>> getSortedMosts(Map<String, Integer> mostsIn30Games) {
		return mostsIn30Games
				.entrySet().stream()
				.sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
				.collect(Collectors.toList());
	}

	private Map<String, Integer> getMostsIn30Games(String summonerName, List<MatchInfoDto> matchInfoDtos) {
		Map<String, Integer> mostsIn30Games = new HashMap<>();

		for (MatchInfoDto matchInfoDto : matchInfoDtos) {
			String championName = getChampionName(summonerName, matchInfoDto);
			mostsIn30Games.put(championName, addChampionPlayCount(mostsIn30Games, championName));
		}

		return mostsIn30Games;
	}

	private Integer addChampionPlayCount(Map<String, Integer> mostsIn30Games, String championName) {
		if (mostsIn30Games.containsKey(championName)) {
			return mostsIn30Games.get(championName) + 1;
		}
		return 1;
	}

	private String getChampionName(String summonerName, MatchInfoDto matchInfoDto) {
		return matchInfoDto
				.getParticipants()
				.stream().filter(participantDto -> participantDto.getSummonerName().equals(summonerName))
				.collect(Collectors.toList())
				.get(0)
				.getChampionName();
	}
}
