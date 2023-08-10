package com.lolup.riot.match.application;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.lolup.duo.application.dto.ChampionStatDto;
import com.lolup.riot.match.application.dto.MatchDto;
import com.lolup.riot.match.application.dto.ParticipantDto;
import com.lolup.riot.match.application.dto.RecentMatchStatsDto;
import com.lolup.riot.match.exception.InvalidMatchIdException;
import com.lolup.riot.match.exception.InvalidPuuIdException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MatchService {

	private static final String MATCH_ID_REQUEST_URI = "/lol/match/v5/matches/by-puuid/{puuId}/ids?queue={queueId}&start=0&count={count}&api_key={apiKey}";
	private static final String MATCH_REQUEST_URI = "/lol/match/v5/matches/{matchId}?api_key={apiKey}";
	private static final int TOTAL_MATCH_COUNT = 10;

	private final WebClient webClient;
	private final String apiKey;

	public MatchService(@Qualifier("matchWebClient") final WebClient webClient,
						@Value("${security.riot.api-key}") final String apiKey) {
		this.webClient = webClient;
		this.apiKey = apiKey;
	}

	public RecentMatchStatsDto requestRecentMatchStats(final String puuId) {
		String[] matchIds = requestMatchIds(puuId, QueueType.RANKED_SOLO.getQueueId());
		List<MatchDto> matchDtos = requestMatchHistories(matchIds);
		List<ParticipantDto> participants = extractParticipantsFrom(matchDtos, puuId);
		List<ChampionStatDto> championStats = extractChampionStats(participants);
		double latestWinRate = calculateLatestWinRate(participants);

		return new RecentMatchStatsDto(latestWinRate, championStats);
	}

	private String[] requestMatchIds(final String puuId, final int queueId) {
		try {
			return webClient.get()
					.uri(MATCH_ID_REQUEST_URI, puuId, queueId, TOTAL_MATCH_COUNT, apiKey)
					.retrieve()
					.bodyToMono(String[].class)
					.block();
		} catch (RuntimeException e) {
			throw new InvalidPuuIdException();
		}
	}

	private List<MatchDto> requestMatchHistories(final String[] matchIds) {
		return Flux.fromArray(matchIds)
				.flatMap(this::requestMatchMono)
				.collectList()
				.blockOptional()
				.orElseThrow(InvalidMatchIdException::new);
	}

	private Mono<MatchDto> requestMatchMono(final String matchId) {
		try {
			return webClient.get()
					.uri(MATCH_REQUEST_URI, matchId, apiKey)
					.retrieve()
					.bodyToMono(MatchDto.class);
		} catch (RuntimeException e) {
			throw new InvalidMatchIdException();
		}
	}

	private List<ParticipantDto> extractParticipantsFrom(final List<MatchDto> matchDtos, final String puuid) {
		return matchDtos.stream()
				.map(matchDto -> findByPuuid(matchDto.getParticipants(), puuid))
				.collect(Collectors.toList());
	}

	private ParticipantDto findByPuuid(final List<ParticipantDto> participantDtos, final String puuid) {
		return participantDtos.stream()
				.filter(participantDto -> participantDto.hasSamePuuid(puuid))
				.findFirst()
				.orElseThrow(InvalidPuuIdException::new);
	}

	private List<ChampionStatDto> extractChampionStats(final List<ParticipantDto> participantDtos) {
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

	private double calculateLatestWinRate(List<ParticipantDto> participantDtos) {
		int totalMatchCount = participantDtos.size();
		long winCount = participantDtos.stream()
				.filter(ParticipantDto::isWin)
				.count();

		return (double)winCount / totalMatchCount;
	}
}
