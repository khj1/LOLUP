package com.lolup.lolup_project.duo.application.dto;

import java.util.List;

import com.lolup.lolup_project.duo.domain.SummonerRankInfo;
import com.lolup.lolup_project.riot.summoner.domain.MostInfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SummonerDto {

	private int profileIconId;
	private double latestWinRate;
	private SummonerRankInfo info;
	private List<MostInfo> most3;

	public SummonerDto(final int profileIconId, final double latestWinRate, final SummonerRankInfo info,
					   final List<MostInfo> most3) {
		this.profileIconId = profileIconId;
		this.latestWinRate = latestWinRate;
		this.info = info;
		this.most3 = most3;
	}
}
