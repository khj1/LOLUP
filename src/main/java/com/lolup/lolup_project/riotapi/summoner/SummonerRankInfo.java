package com.lolup.lolup_project.riotapi.summoner;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class SummonerRankInfo {

	private int iconId;
	private String summonerName;
	private String tier;
	private String rank;
	private int wins;
	private int losses;

	@Builder
	public SummonerRankInfo(String summonerName, String tier, String rank, int wins, int losses) {
		this.summonerName = summonerName;
		this.tier = tier;
		this.rank = rank;
		this.wins = wins;
		this.losses = losses;
	}
}
