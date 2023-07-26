package com.lolup.riot.match.application.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipantDto {
	private String summonerName;
	private String championName;
	private int championId;
	private int teamId;
	private boolean win;

	@Builder
	public ParticipantDto(final String summonerName, final String championName, final int championId, final int teamId,
						  final boolean win) {
		this.summonerName = summonerName;
		this.championName = championName;
		this.championId = championId;
		this.teamId = teamId;
		this.win = win;
	}

	public boolean hasSameSummonerName(final String summonerName) {
		return this.summonerName.equals(summonerName);
	}
}
