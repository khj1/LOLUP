package com.lolup.lolup_project.duo.application.dto;

import java.util.List;

import com.lolup.lolup_project.duo.domain.SummonerRankInfo;
import com.lolup.lolup_project.riot.summoner.domain.ChampionStat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

//TODO ChampionStat 엔티티를 DTO로 변경 필요
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SummonerDto {

	private int profileIconId;
	private double latestWinRate;
	private SummonerRankInfo info;
	private List<ChampionStat> championStats;

	public SummonerDto(final int profileIconId, final double latestWinRate, final SummonerRankInfo info,
					   final List<ChampionStat> championStats) {
		this.profileIconId = profileIconId;
		this.latestWinRate = latestWinRate;
		this.info = info;
		this.championStats = championStats;
	}
}
