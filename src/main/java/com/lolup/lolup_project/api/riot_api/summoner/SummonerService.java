package com.lolup.lolup_project.api.riot_api.summoner;

import com.lolup.lolup_project.api.APIConst;
import com.lolup.lolup_project.api.riot_api.match.MatchDto;
import com.lolup.lolup_project.api.riot_api.match.MatchReferenceDTO;
import com.lolup.lolup_project.api.riot_api.match.MatchlistDTO;
import com.lolup.lolup_project.api.riot_api.resource.ChampionResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SummonerService {

    private final WebClient webClient;

    public SummonerAccountDTO getSummonerAccountInfo(String summonerName) {
        return webClient
                .get()
                .uri("/lol/summoner/v4/summoners/by-name/" + summonerName + "?api_key=" + APIConst.riot_apiKey)
                .retrieve()
                .bodyToMono(SummonerAccountDTO.class)
                .block();
    }

    public SummonerRankDTO getSummonerTotalSoloRankInfo(String summonerName) {
        SummonerAccountDTO summonerAccountInfo = getSummonerAccountInfo(summonerName);
        String id = summonerAccountInfo.getId();
        int profileIconId = summonerAccountInfo.getProfileIconId();

        SummonerRankDTO summonerRankDTO = webClient
                .get()
                .uri("/lol/league/v4/entries/by-summoner/" + id + "?api_key=" + APIConst.riot_apiKey)
                .retrieve()
                .bodyToFlux(SummonerRankDTO.class)
                .blockFirst();

        if (summonerRankDTO == null) {
            summonerRankDTO = SummonerRankDTO.builder()
                    .summonerName(summonerName)
                    .tier("UNRANKED")
                    .rank("언랭크")
                    .wins(0)
                    .losses(0)
                    .build();
        }

        summonerRankDTO.setProfileIconId(profileIconId);

        return summonerRankDTO;
    }

    public String[] getGameVersion() {
        return webClient
                .get()
                .uri("https://ddragon.leagueoflegends.com/api/versions.json")
                .retrieve()
                .bodyToMono(String[].class)
                .block();
    }

    public List<MatchReferenceDTO> getLatestMatches(String summonerName) {
        List<MatchReferenceDTO> matches = getMatchReferences(summonerName);

        for (MatchReferenceDTO match : matches) {
            getLatestMatchInfo(match);
        }

        return matches;
    }

    private String getLatestWinRate(List<MatchReferenceDTO> matches) {
        long winCount = matches.stream()
                .filter(matchReferenceDTO -> matchReferenceDTO.getWin().equals("Win")).count();

        return (double) (winCount * 10) + "%";
    }

    private Map<String, Integer> getLatestMost3(List<MatchReferenceDTO> matches) {
        Map<String, Integer> most10 = getLatestMost10(matches);
        Stream<Map.Entry<String, Integer>> sortedMap = getSortedMap(most10);
        List<Map.Entry<String, Integer>> most3Entries = getMost3Entries(sortedMap);

        return getMost3Map(most3Entries);

    }

    private Map<String, Integer> getMost3Map(List<Map.Entry<String, Integer>> entries) {
        Map<String, Integer> most3 = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : entries) {
            most3.put(entry.getKey(), entry.getValue());
        }
        return most3;
    }

    private List<Map.Entry<String, Integer>> getMost3Entries(Stream<Map.Entry<String, Integer>> sortedMap) {
        return sortedMap.collect(Collectors.toList())
                .subList(0, 3);
    }

    private Stream<Map.Entry<String, Integer>> getSortedMap(Map<String, Integer> most10) {
        return most10.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed());
    }

    private Map<String, Integer> getLatestMost10(List<MatchReferenceDTO> matches) {
        Map<String, Integer> most10 = new HashMap<>();

        for (MatchReferenceDTO match : matches) {
            int championId = match.getChampion();
            String championName = ChampionResource.getChampionNameById(championId);

            if (most10.containsKey(championName)) {
                most10.put(championName, most10.get(championName) + 1);
            } else {
                most10.put(championName, 1);
            }
        }
        return most10;
    }

    public Map<String, Object> getSummonerSummaryInfo(String summonerName) {
        Map<String, Object> map = new HashMap<>();

        SummonerRankDTO totalSoloRankInfo = getSummonerTotalSoloRankInfo(summonerName);
        String version = getGameVersion()[0];

        List<MatchReferenceDTO> matches = getLatestMatches(summonerName);
        String latestWinRate = getLatestWinRate(matches);
        Map<String, Integer> latestMost3 = getLatestMost3(matches);

        map.put("version", version);
        map.put("info", totalSoloRankInfo);
        map.put("latestWinRate", latestWinRate);
        map.put("most3", latestMost3);

        return map;
    }

    private List<MatchReferenceDTO> getMatchReferences(String summonerName) {
        String accountId = getSummonerAccountInfo(summonerName).getAccountId();
        return webClient
                .get()
                .uri("/lol/match/v4/matchlists/by-account/" + accountId + "?api_key=" + APIConst.riot_apiKey + "&queue=" + 420 + "&endIndex="  + 10)
                .retrieve()
                .bodyToFlux(MatchlistDTO.class)
                .collectList()
                .block()
                .get(0)
                .getMatches();
    }

    public void getLatestMatchInfo(MatchReferenceDTO matchReferenceDTO) {
        Long gameId = matchReferenceDTO.getGameId();
        int championId = matchReferenceDTO.getChampion();

        MatchDto matchDto = webClient
                .get()
                .uri("https://kr.api.riotgames.com/lol/match/v4/matches/" + gameId + "?api_key=" + APIConst.riot_apiKey)
                .retrieve()
                .bodyToMono(MatchDto.class)
                .block();

        int teamId = getTeamId(championId, matchDto);
        String win = getWin(teamId, matchDto);

        matchReferenceDTO.setWin(win);
    }

    private String getWin(int teamId, MatchDto matchDto) {
        return matchDto
                .getTeams()
                .stream()
                .filter(teamStatsDto -> teamStatsDto.getTeamId() == teamId)
                .collect(Collectors.toList())
                .get(0)
                .getWin();
    }

    private int getTeamId(int championId, MatchDto matchDto) {
        return matchDto
                .getParticipants()
                .stream()
                .filter(participantDto -> participantDto.getChampionId() == championId)
                .collect(Collectors.toList())
                .get(0)
                .getTeamId();
    }

}
