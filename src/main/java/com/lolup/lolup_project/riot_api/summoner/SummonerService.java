package com.lolup.lolup_project.riot_api.summoner;

import com.lolup.lolup_project.riot_api.apiconst.APIConst;
import com.lolup.lolup_project.riot_api.match.MatchDto;
import com.lolup.lolup_project.riot_api.match.MatchReferenceDTO;
import com.lolup.lolup_project.riot_api.match.MatchlistDTO;
import com.lolup.lolup_project.riot_api.resource.ChampionResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummonerService {

    private final WebClient webClient;

    public SummonerAccountDTO getSummonerAccountInfo(String summonerName) {
        return webClient
                .get()
                .uri("/lol/summoner/v4/summoners/by-name/" + summonerName + "?api_key=" + APIConst.apiKey)
                .retrieve()
                .bodyToMono(SummonerAccountDTO.class)
                .block();
    }

    public SummonerRankDTO getSummonerTotalSoloRankInfo(String summonerName) {
        SummonerAccountDTO summonerAccountInfo = getSummonerAccountInfo(summonerName);
        String id = summonerAccountInfo.getId();
        int profileIconId = summonerAccountInfo.getProfileIconId();
        Long summonerLevel = summonerAccountInfo.getSummonerLevel();

        SummonerRankDTO summonerRankDTO = webClient
                .get()
                .uri("/lol/league/v4/entries/by-summoner/" + id + "?api_key=" + APIConst.apiKey)
                .retrieve()
                .bodyToFlux(SummonerRankDTO.class)
                .blockFirst();

        summonerRankDTO.setProfileIconId(profileIconId);
        summonerRankDTO.setSummonerLevel(summonerLevel);

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

    public List<MatchReferenceDTO> getLatestSoloRankHistories(String summonerName) {
        List<MatchReferenceDTO> matches = getMatchReferences(summonerName);

        for (MatchReferenceDTO match : matches) {
            getLatestMatchInfo(match);
        }

        return matches;
    }

    private String getWinRateOfLatestGames(List<MatchReferenceDTO> matches) {
        long winCount = matches.stream()
                .filter(matchReferenceDTO -> matchReferenceDTO.getWin().equals("Win")).count();

        return (double) (winCount * 10) + "%";
    }

    private List<Map.Entry<String, Integer>> getMost3ChampOfLatestGames(List<MatchReferenceDTO> matches) {
        Map<String, Integer> champsPlayed = new HashMap<>();

        for (MatchReferenceDTO match : matches) {
            int championId = match.getChampion();
            String championName = ChampionResource.getChampionNameById(championId);

            if (champsPlayed.containsKey(championName)) {
                champsPlayed.put(championName, champsPlayed.get(championName) + 1);
            } else {
                champsPlayed.put(championName, 1);
            }
        }

        return champsPlayed.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList())
                .subList(0, 3);
    }

    public Map<String, Object> getSummonerSummaryInfo(String summonerName) {
        Map<String, Object> map = new HashMap<>();

        SummonerRankDTO totalSoloRankInfo = getSummonerTotalSoloRankInfo(summonerName);
        String version = getGameVersion()[0];

        List<MatchReferenceDTO> matches = getLatestSoloRankHistories(summonerName);
        String winRateOfLatestGames = getWinRateOfLatestGames(matches);
        List<Map.Entry<String, Integer>> most3ChampOfLatestGames = getMost3ChampOfLatestGames(matches);

        map.put("version", version);
        map.put("totalInfo", totalSoloRankInfo);
        map.put("winRateOfLatestGames", winRateOfLatestGames);
        map.put("most3ChampOfLatestGames", most3ChampOfLatestGames);

        return map;
    }

    private List<MatchReferenceDTO> getMatchReferences(String summonerName) {
        String accountId = getSummonerAccountInfo(summonerName).getAccountId();
        return webClient
                .get()
                .uri("/lol/match/v4/matchlists/by-account/" + accountId + "?api_key=" + APIConst.apiKey + "&queue=" + 420 + "&endIndex="  + 10)
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
                .uri("https://kr.api.riotgames.com/lol/match/v4/matches/" + gameId + "?api_key=" + APIConst.apiKey)
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
