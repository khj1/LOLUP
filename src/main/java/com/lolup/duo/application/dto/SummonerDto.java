package com.lolup.duo.application.dto;

import java.util.List;

import com.lolup.duo.domain.SummonerStat;
import com.lolup.riot.summoner.domain.ChampionStat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SummonerDto {

	private int profileIconId;
	private double latestWinRate;
	private SummonerStat summonerStat;
	private List<ChampionStatDto> championStatDtos;

	public List<ChampionStat> getChampionStats() {
		return championStatDtos.stream()
				.map(ChampionStatDto::toEntity)
				.toList();
	}
}
