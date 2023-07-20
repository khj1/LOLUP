package com.lolup.lolup_project.riotapi;

import org.springframework.stereotype.Service;

import com.lolup.lolup_project.riotapi.match.MatchService;
import com.lolup.lolup_project.riotapi.match.RecentMatchStatsDto;
import com.lolup.lolup_project.riotapi.summoner.SummonerDto;
import com.lolup.lolup_project.riotapi.summoner.SummonerRankInfo;
import com.lolup.lolup_project.riotapi.summoner.SummonerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RiotService {

	private final SummonerService summonerService;
	private final MatchService matchService;

	public SummonerDto find(String summonerName) {
		SummonerRankInfo info = summonerService.getSummonerTotalSoloRankInfo(summonerName);
		RecentMatchStatsDto recentMatchStats = matchService.getRecentMatchStats(summonerName);

		return new SummonerDto(recentMatchStats.getLatestWinRate(), info, recentMatchStats.getMost3());
	}

}
