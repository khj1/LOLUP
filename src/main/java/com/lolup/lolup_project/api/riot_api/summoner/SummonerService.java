package com.lolup.lolup_project.api.riot_api.summoner;

import com.lolup.lolup_project.api.APIConst;
import com.lolup.lolup_project.api.riot_api.match.MatchDto;
import com.lolup.lolup_project.api.riot_api.match.MatchReferenceDTO;
import com.lolup.lolup_project.api.riot_api.match.MatchlistDTO;
import com.lolup.lolup_project.api.riot_api.resource.ChampionResource;
import lombok.RequiredArgsConstructor;
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

    private SummonerAccountDto getAccountInfo(String summonerName) {
        return webClient
                .get()
                .uri("/lol/summoner/v4/summoners/by-name/" + summonerName + "?api_key=" + APIConst.riot_apiKey)
                .retrieve()
                .bodyToMono(SummonerAccountDto.class)
                .block();
    }

    private SummonerRankDto getSummonerTotalSoloRankInfo(String summonerName) {
        SummonerAccountDto summonerAccountInfo = getAccountInfo(summonerName);
        String id = summonerAccountInfo.getId();
        int iconId = summonerAccountInfo.getIconId();

        SummonerRankDto summonerRankDTO = getRankInfo(id);

        if (summonerRankDTO == null) {
            summonerRankDTO = getUnrankedInfo(summonerName);
        }

        if (summonerRankDTO == null) {
            summonerRankDTO = SummonerRankDto.builder()
                    .summonerName(summonerName)
                    .tier("UNRANKED")
                    .rank("언랭크")
                    .win(0)
                    .lose(0)
                    .build();
        }

        summonerRankDTO.setIconId(iconId);

        return summonerRankDTO;
    }

    private SummonerRankDto getUnrankedInfo(String summonerName) {
        return SummonerRankDto.builder()
                .summonerName(summonerName)
                .tier("UNRANKED")
                .rank("언랭크")
                .win(0)
                .lose(0)
                .build();
    }

    private SummonerRankDto getRankInfo(String id) {
        return webClient
                .get()
                .uri("/lol/league/v4/entries/by-summoner/" + id + "?api_key=" + APIConst.riot_apiKey)
                .retrieve()
                .bodyToFlux(SummonerRankDto.class)
                .blockFirst();
    }

    private String[] getGameVersion() {
        return webClient
                .get()
                .uri("https://ddragon.leagueoflegends.com/api/versions.json")
                .retrieve()
                .bodyToMono(String[].class)
                .block();
    }

    private List<MatchReferenceDTO> getLatestMatches(String summonerName) {
        List<MatchReferenceDTO> matches = getMatchReferences(summonerName);

        for (MatchReferenceDTO match : matches) {
            setWinCount(match);
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

    public SummonerDto find(String summonerName) {

        SummonerRankDto info = getSummonerTotalSoloRankInfo(summonerName);
        String version = getGameVersion()[0];

        List<MatchReferenceDTO> matches = getLatestMatches(summonerName);
        String latestWinRate = getLatestWinRate(matches);
        Map<String, Integer> most3 = getLatestMost3(matches);

        return SummonerDto.builder()
                .version(version)
                .latestWinRate(latestWinRate)
                .info(info)
                .most3(most3).build();

    }

    private List<MatchReferenceDTO> getMatchReferences(String summonerName) {
        String accountId = getAccountInfo(summonerName).getAccountId();
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

    private void setWinCount(MatchReferenceDTO matchReferenceDTO) {
        Long gameId = matchReferenceDTO.getGameId();
        int championId = matchReferenceDTO.getChampion();

        MatchDto matchDto = getMatchInfo(gameId);
        int teamId = getTeamId(championId, matchDto);
        String win = getWin(teamId, matchDto);

        matchReferenceDTO.setWin(win);
    }

    private MatchDto getMatchInfo(Long gameId) {
        return webClient
                .get()
                .uri("https://kr.api.riotgames.com/lol/match/v4/matches/" + gameId + "?api_key=" + APIConst.riot_apiKey)
                .retrieve()
                .bodyToMono(MatchDto.class)
                .block();
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
