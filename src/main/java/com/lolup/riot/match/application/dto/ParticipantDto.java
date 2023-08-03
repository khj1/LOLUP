package com.lolup.riot.match.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipantDto {

	private String summonerName;
	private String championName;
	private int championId;
	private int teamId;
	private boolean win;

	public boolean hasSameSummonerName(final String summonerName) {
		return this.summonerName.equals(summonerName);
	}
}
