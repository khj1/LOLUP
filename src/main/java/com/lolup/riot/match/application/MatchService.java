package com.lolup.riot.match.application;

import java.util.Collection;
import java.util.Comparator;
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

import com.lolup.duo.application.dto.ChampionStatDto;
import com.lolup.riot.match.application.dto.MatchDto;
import com.lolup.riot.match.application.dto.ParticipantDto;
import com.lolup.riot.match.application.dto.RecentMatchStatsDto;
import com.lolup.riot.match.exception.InvalidMatchIdException;
import com.lolup.riot.match.exception.InvalidPuuIdException;

@Service
public class MatchService {

	private static final String MATCH_ID_REQUEST_URI = "/lol/match/v5/matches/by-puuid/{puuId}/ids?queue={queueId}&start=0&count={count}&api_key={apiKey}";
	private static final String MATCH_REQUEST_URI = "/lol/match/v5/matches/{matchId}?api_key={apiKey}";
	private static final int TOTAL_MATCH_COUNT = 10;
	private static final int FROM_INDEX_INCLUSIVE = 0;
	private static final int TO_INDEX_EXCLUSIVE = TOTAL_MATCH_COUNT;

	private final WebClient webClient;
	private final String apiKey;

	public MatchService(@Qualifier("matchWebClient") final WebClient webClient,
						@Value("${security.riot.api-key}") final String apiKey) {
		this.webClient = webClient;
		this.apiKey = apiKey;
	}

	public RecentMatchStatsDto requestRecentMatchStats(final String puuId) {
		List<ParticipantDto> participants = getParticipants(puuId);
		List<ChampionStatDto> championStats = getMostPlayedChampions(participants);
		double latestWinRate = getLatestWinRate(participants);

		return new RecentMatchStatsDto(latestWinRate, championStats);
	}

	private List<ParticipantDto> getParticipants(final String puuId) {
		List<String> soloMatchIds = requestMatchIds(puuId, QueueType.RANKED_SOLO.getQueueId());
		List<String> teamMatchIds = requestMatchIds(puuId, QueueType.RANKED_TEAM.getQueueId());
		List<String> matchIds = mergeMatchIds(soloMatchIds, teamMatchIds);

		return extractParticipantDtoBy(puuId, matchIds);
	}

	private List<String> requestMatchIds(final String puuId, final int queueId) {
		try {
			return webClient.get()
					.uri(MATCH_ID_REQUEST_URI, puuId, queueId, TOTAL_MATCH_COUNT, apiKey)
					.retrieve()
					.bodyToMono(new ParameterizedTypeReference<List<String>>() {
					})
					.block();
		} catch (RuntimeException e) {
			throw new InvalidPuuIdException();
		}
	}

	private List<String> mergeMatchIds(final List<String> soloMatchIds, final List<String> teamMatchIds) {
		List<String> matchIds = Stream.of(soloMatchIds, teamMatchIds)
				.flatMap(Collection::stream)
				.toList();

		if (matchIds.size() > TOTAL_MATCH_COUNT) {
			matchIds = matchIds.subList(FROM_INDEX_INCLUSIVE, TO_INDEX_EXCLUSIVE);
		}
		return matchIds;
	}

	private List<ParticipantDto> extractParticipantDtoBy(final String puuid, final List<String> recentMatchIds) {
		return recentMatchIds.stream()
				.map(this::requestMatchDto)
				.map(MatchDto::getParticipants)
				.map(participants -> findByPuuid(participants, puuid))
				.collect(Collectors.toList());
	}

	private MatchDto requestMatchDto(final String matchId) {
		try {
			return webClient.get()
					.uri(MATCH_REQUEST_URI, matchId, apiKey)
					.retrieve()
					.bodyToMono(MatchDto.class)
					.block();
		} catch (RuntimeException e) {
			throw new InvalidMatchIdException();
		}
	}

	private ParticipantDto findByPuuid(final List<ParticipantDto> participantDtos, final String puuid) {
		return participantDtos.stream()
				.filter(participantDto -> participantDto.hasSamePuuid(puuid))
				.findFirst()
				.orElseThrow(InvalidPuuIdException::new);
	}

	private List<ChampionStatDto> getMostPlayedChampions(final List<ParticipantDto> participantDtos) {
		Map<String, Long> mostPlayedChampions = countPlayedChampion(participantDtos);
		Map<String, Long> sortedMost = sortByPlayedCount(mostPlayedChampions);

		return sortedMost.entrySet().stream()
				.map(entry -> ChampionStatDto.create(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	private Map<String, Long> countPlayedChampion(final List<ParticipantDto> participantDtos) {
		return participantDtos.stream()
				.collect(Collectors.groupingBy(ParticipantDto::getChampionName, Collectors.counting()));
	}

	private LinkedHashMap<String, Long> sortByPlayedCount(final Map<String, Long> mostPlayedChampions) {
		return mostPlayedChampions.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}

	private double getLatestWinRate(List<ParticipantDto> participantDtos) {
		int totalMatchCount = participantDtos.size();
		long winCount = participantDtos.stream()
				.filter(ParticipantDto::isWin)
				.count();

		return (double)winCount / totalMatchCount;
	}
}
