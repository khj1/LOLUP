package com.lolup.riot.match.application.dto;

import java.util.List;

import com.lolup.duo.application.dto.ChampionStatDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecentMatchStatsDto {

	private double latestWinRate;
	private List<ChampionStatDto> championStats;
}
