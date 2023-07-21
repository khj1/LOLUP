package com.lolup.lolup_project.riotapi.summoner;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SummonerDto {

	private double latestWinRate;
	private SummonerRankInfo info;
	private List<MostInfo> most3;

	public SummonerDto(final double latestWinRate, final SummonerRankInfo info, final List<MostInfo> most3) {
		this.latestWinRate = latestWinRate;
		this.info = info;
		this.most3 = most3;
	}
}
