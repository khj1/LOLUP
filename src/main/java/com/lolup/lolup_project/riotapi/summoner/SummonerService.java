package com.lolup.lolup_project.riotapi.summoner;

import com.lolup.lolup_project.riotapi.APIConst;
import com.lolup.lolup_project.riotapi.match.MatchDto;
import com.lolup.lolup_project.riotapi.match.MatchInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummonerService {

    private final WebClient webClient;

    public SummonerAccountDto getAccountInfo(String summonerName) {
        return webClient
                .get()
                .uri("/lol/summoner/v4/summoners/by-name/" + summonerName + "?api_key=" + APIConst.riot_apiKey)
                .retrieve()
                .bodyToMono(SummonerAccountDto.class)
                .block();
    }

    private SummonerRankInfo getSummonerTotalSoloRankInfo(String summonerName) {
        SummonerAccountDto summonerAccountInfo = getAccountInfo(summonerName);
        String id = summonerAccountInfo.getId();
        int iconId = summonerAccountInfo.getProfileIconId();

        SummonerRankInfoDto summonerRankInfoDto = getRankInfoDto(id);

        if (summonerRankInfoDto == null) {
            return getUnrankedInfo(summonerName);
        }

        return getRankInfo(summonerRankInfoDto, iconId);
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

    private SummonerRankInfo getUnrankedInfo(String summonerName) {
        return SummonerRankInfo.builder()
                .summonerName(summonerName)
                .tier("UNRANKED")
                .rank("언랭크")
                .wins(0)
                .losses(0)
                .build();
    }

    private SummonerRankInfoDto getRankInfoDto(String id) {
        return webClient
                .get()
                .uri("/lol/league/v4/entries/by-summoner/" + id + "?api_key=" + APIConst.riot_apiKey)
                .retrieve()
                .bodyToFlux(SummonerRankInfoDto.class)
                .blockFirst();
    }

    public String getGameVersion() {
        String[] versions = webClient
                .get()
                .uri("https://ddragon.leagueoflegends.com/api/versions.json")
                .retrieve()
                .bodyToMono(String[].class)
                .block();

        return versions[0];
    }

    private List<MatchInfoDto> getMatchInfos(String summonerName) {

        List<MatchInfoDto> matchInfoDtos = new ArrayList<>();
        String[] matchIds = getMatchIds(summonerName);

        Arrays.stream(matchIds).forEach(matchId -> matchInfoDtos.add(getMatchDto(matchId).getInfo()));

        return matchInfoDtos;
    }

    private String getLatestWinRate(String summonerName, List<MatchInfoDto> matchInfoDtos) {
        long winCount = matchInfoDtos.stream()
                .filter(matchInfo -> getWin(summonerName, matchInfo)).count();

        return ((double)winCount / 30) * 100 + "%";
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
        } else if (sortedMosts.size() == 2) {
            return sortedMosts.subList(0, 2);
        } else {
            return sortedMosts.subList(0, 1);
        }
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

            if (mostsIn30Games.containsKey(championName)) {
                mostsIn30Games.put(championName, mostsIn30Games.get(championName) + 1);
            } else {
                mostsIn30Games.put(championName, 1);
            }
        }

        return mostsIn30Games;
    }

    private String[] getMatchIds(String summonerName) {
        String puuid = getAccountInfo(summonerName).getPuuid();
        return webClient
                .get()
                .uri("https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/" + puuid + "/ids?api_key=" + APIConst.riot_apiKey + "&start=0&count=30")
                .retrieve()
                .bodyToMono(String[].class)
                .block();
    }

    public SummonerDto find(String summonerName) {

        SummonerRankInfo info = getSummonerTotalSoloRankInfo(summonerName);
        List<MatchInfoDto> matchInfoDtos = getMatchInfos(summonerName);
        String latestWinRate = getLatestWinRate(summonerName, matchInfoDtos);
        List<MostInfo> most3 = getLatestMost3(summonerName, matchInfoDtos);

        return new SummonerDto(latestWinRate, info, most3);
    }

    private MatchDto getMatchDto(String matchId) {
        return webClient
                .get()
                .uri("https://asia.api.riotgames.com/lol/match/v5/matches/" + matchId + "?api_key=" + APIConst.riot_apiKey)
                .retrieve()
                .bodyToMono(MatchDto.class)
                .block();
    }

    private Boolean getWin(String summonerName, MatchInfoDto matchInfoDto) {
        return matchInfoDto
                .getParticipants()
                .stream().filter(participantDto -> participantDto.getSummonerName().equals(summonerName))
                .collect(Collectors.toList())
                .get(0)
                .isWin();
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
