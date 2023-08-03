package com.lolup.duo.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SummonerStat {

	private String summonerName;
	private SummonerTier tier;
	private SummonerRank rank;
	private int wins;
	private int losses;

	@Builder
	public SummonerStat(final String summonerName, final SummonerTier tier, final SummonerRank rank, final int wins,
						final int losses) {
		this.summonerName = summonerName;
		this.tier = tier;
		this.rank = rank;
		this.wins = wins;
		this.losses = losses;
	}
}
