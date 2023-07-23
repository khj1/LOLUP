package com.lolup.lolup_project.riot.match.application;

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

import com.lolup.lolup_project.riot.match.application.dto.MatchDto;
import com.lolup.lolup_project.riot.match.application.dto.MatchInfoDto;
import com.lolup.lolup_project.riot.match.application.dto.ParticipantDto;
import com.lolup.lolup_project.riot.match.application.dto.RecentMatchStatsDto;
import com.lolup.lolup_project.riot.match.exception.NoSuchSummonerException;
import com.lolup.lolup_project.riot.summoner.domain.MostInfo;

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

	public RecentMatchStatsDto getRecentMatchStats(final String summonerName, final String puuId) {
		List<MatchInfoDto> matchInfoDtos = getMatchInfos(puuId);
		List<ParticipantDto> participantDtos = extractParticipantDtoBy(summonerName, matchInfoDtos);
		List<MostInfo> most3 = getMostPlayedChampions(participantDtos);
		double latestWinRate = getLatestWinRate(participantDtos);

		return new RecentMatchStatsDto(latestWinRate, most3);
	}

	private List<ParticipantDto> extractParticipantDtoBy(final String summonerName,
														 final List<MatchInfoDto> matchInfoDtos) {
		return matchInfoDtos.stream()
				.map(MatchInfoDto::getParticipants)
				.map(participantDtos -> findBySummonerName(participantDtos, summonerName))
				.collect(Collectors.toList());
	}

	private ParticipantDto findBySummonerName(final List<ParticipantDto> participantDtos, final String summonerName) {
		return participantDtos.stream()
				.filter(participantDto -> participantDto.hasSameSummonerName(summonerName))
				.findFirst()
				.orElseThrow(NoSuchSummonerException::new);
	}

	private List<MatchInfoDto> getMatchInfos(final String puuId) {
		List<String> soloMatchIds = getMatchIds(puuId, QueueType.RANKED_SOLO.getQueueId());
		List<String> teamMatchIds = getMatchIds(puuId, QueueType.RANKED_TEAM.getQueueId());
		List<String> matchIds = mergeMatchIds(soloMatchIds, teamMatchIds);
		List<String> recentMatchIds = matchIds.subList(FROM_INDEX_INCLUSIVE, TO_INDEX_EXCLUSIVE);

		return recentMatchIds.stream()
				.map(this::getMatchDto)
				.map(MatchDto::getInfo)
				.toList();
	}

	private List<String> mergeMatchIds(final List<String> soloMatchIds, final List<String> teamMatchIds) {
		return Stream.of(soloMatchIds, teamMatchIds)
				.flatMap(Collection::stream)
				.toList();
	}

	private List<String>
	getMatchIds(final String puuId, final int queueId) {
		return webClient
				.get()
				.uri(MATCH_ID_REQUEST_URI, puuId, queueId, apiKey)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<String>>() {
				})
				.block();
	}

	private MatchDto getMatchDto(final String matchId) {
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

	private List<MostInfo> getMostPlayedChampions(final List<ParticipantDto> participantDtos) {
		Map<String, Long> mostPlayedChampions = countPlayedChampion(participantDtos);
		Map<String, Long> sortedMost = sortByPlayedCount(mostPlayedChampions);

		return sortedMost.entrySet().stream()
				.map(entry -> MostInfo.create(entry.getKey(), entry.getValue()))
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
