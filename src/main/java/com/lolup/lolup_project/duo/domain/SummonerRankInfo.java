package com.lolup.lolup_project.duo.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SummonerRankInfo {

	private int iconId;
	private String summonerName;
	private String tier;
	private String rank;
	private int wins;
	private int losses;

	@Builder
	public SummonerRankInfo(final String summonerName, final String tier, final String rank, final int wins,
							final int losses) {
		this.summonerName = summonerName;
		this.tier = tier;
		this.rank = rank;
		this.wins = wins;
		this.losses = losses;
	}
}
