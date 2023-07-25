package com.lolup.lolup_project.riot.match.application.dto;

import java.util.List;

import com.lolup.lolup_project.riot.summoner.domain.ChampionStat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecentMatchStatsDto {

	private double latestWinRate;
	private List<ChampionStat> championStats;

	public RecentMatchStatsDto(final double latestWinRate, final List<ChampionStat> championStats) {
		this.latestWinRate = latestWinRate;
		this.championStats = championStats;
	}
}
