package com.lolup.lolup_project.riotapi.match;

import java.util.List;

import com.lolup.lolup_project.riotapi.summoner.MostInfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecentMatchStatsDto {

	private double latestWinRate;
	private List<MostInfo> most3;

	public RecentMatchStatsDto(final double latestWinRate, final List<MostInfo> most3) {
		this.latestWinRate = latestWinRate;
		this.most3 = most3;
	}
}
