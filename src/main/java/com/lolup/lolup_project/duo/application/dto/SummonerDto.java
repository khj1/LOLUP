package com.lolup.lolup_project.duo.application.dto;

import java.util.List;

import com.lolup.lolup_project.duo.domain.SummonerStat;
import com.lolup.lolup_project.riot.summoner.domain.ChampionStat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SummonerDto {

	private int profileIconId;
	private double latestWinRate;
	private SummonerStat summonerStat;
	private List<ChampionStat> championStats;

	public SummonerDto(final int profileIconId, final double latestWinRate, final SummonerStat summonerStat,
					   final List<ChampionStat> championStats) {
		this.profileIconId = profileIconId;
		this.latestWinRate = latestWinRate;
		this.summonerStat = summonerStat;
		this.championStats = championStats;
	}
}
