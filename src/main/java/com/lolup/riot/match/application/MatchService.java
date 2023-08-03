package com.lolup.riot.match.application;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.lolup.riot.match.application.dto.MatchDto;
import com.lolup.riot.match.application.dto.ParticipantDto;
import com.lolup.riot.match.application.dto.RecentMatchStatsDto;
import com.lolup.riot.match.exception.NoSuchSummonerException;
import com.lolup.riot.summoner.domain.ChampionStat;

@Service
public class MatchService {

	private static final String MATCH_ID_REQUEST_URI = "/lol/match/v5/matches/by-puuid/{puuId}/ids?queue={queueId}&start=0&count=30&api_key={apiKey}";
	private static final String MATCH_REQUEST_URI = "/lol/match/v5/matches/{matchId}?api_key={apiKey}";
	private static final int TOTAL_MATCH_COUNT = 30;
	private static final int FROM_INDEX_INCLUSIVE = 0;
	private static final int TO_INDEX_EXCLUSIVE = TOTAL_MATCH_COUNT;

	private final WebClient webClient;
	private final String apiKey;

	public MatchService(@Qualifier("matchWebClient") final WebClient webClient,
						@Value("${security.riot.api-key}") final String apiKey) {
		this.webClient = webClient;
		this.apiKey = apiKey;
	}

	public RecentMatchStatsDto requestRecentMatchStats(final String summonerName, final String puuId) {
		List<ParticipantDto> participantDtos = getParticipants(puuId, summonerName);
		List<ChampionStat> championStats = getMostPlayedChampions(participantDtos);
		double latestWinRate = getLatestWinRate(participantDtos);

		return new RecentMatchStatsDto(latestWinRate, championStats);
	}

	private ParticipantDto findBySummonerName(final List<ParticipantDto> participantDtos, final String summonerName) {
		return participantDtos.stream()
				.filter(participantDto -> participantDto.hasSameSummonerName(summonerName))
				.findFirst()
				.orElseThrow(NoSuchSummonerException::new);
	}

	private List<ParticipantDto> getParticipants(final String puuId, final String summonerName) {
		List<String> soloMatchIds = requestMatchIds(puuId, QueueType.RANKED_SOLO.getQueueId());
		List<String> teamMatchIds = requestMatchIds(puuId, QueueType.RANKED_TEAM.getQueueId());
		List<String> matchIds = mergeMatchIds(soloMatchIds, teamMatchIds);
		List<String> recentMatchIds = matchIds.subList(FROM_INDEX_INCLUSIVE, TO_INDEX_EXCLUSIVE);

		return extractParticipantDtoBy(summonerName, recentMatchIds);
	}

	private List<ParticipantDto> extractParticipantDtoBy(final String summonerName, final List<String> recentMatchIds) {
		return recentMatchIds.stream()
				.map(this::requestMatchDto)
				.map(MatchDto::getParticipants)
				.map(participantDtos -> findBySummonerName(participantDtos, summonerName))
				.collect(Collectors.toList());
	}

	private List<String> mergeMatchIds(final List<String> soloMatchIds, final List<String> teamMatchIds) {
		return Stream.of(soloMatchIds, teamMatchIds)
				.flatMap(Collection::stream)
				.toList();
	}

	private List<String> requestMatchIds(final String puuId, final int queueId) {
		return webClient
				.get()
				.uri(MATCH_ID_REQUEST_URI, puuId, queueId, apiKey)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<String>>() {
				})
				.block();
	}

	private MatchDto requestMatchDto(final String matchId) {
		return webClient
				.get()
				.uri(MATCH_REQUEST_URI, matchId, apiKey)
				.retrieve()
				.bodyToMono(MatchDto.class)
				.block();
	}

	private double getLatestWinRate(List<ParticipantDto> participantDtos) {
		long winCount = participantDtos.stream()
				.filter(ParticipantDto::isWin)
				.count();

		return (double)winCount / TOTAL_MATCH_COUNT;
	}

	private List<ChampionStat> getMostPlayedChampions(final List<ParticipantDto> participantDtos) {
		Map<String, Long> mostPlayedChampions = countPlayedChampion(participantDtos);
		Map<String, Long> sortedMost = sortByPlayedCount(mostPlayedChampions);

		return sortedMost.entrySet().stream()
				.map(entry -> ChampionStat.create(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	private Map<String, Long> countPlayedChampion(final List<ParticipantDto> participantDtos) {
		return participantDtos.stream()
				.collect(Collectors.groupingBy(ParticipantDto::getChampionName, Collectors.counting()));
	}

	private LinkedHashMap<String, Long> sortByPlayedCount(final Map<String, Long> mostPlayedChampions) {
		return mostPlayedChampions.entrySet().stream()
				.sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}
}
