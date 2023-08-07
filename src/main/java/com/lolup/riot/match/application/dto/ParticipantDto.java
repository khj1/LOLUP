package com.lolup.riot.match.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

//TODO PUUID를 필드에 추가한다. (summonerName은 제거해도 무방해보임)
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipantDto {

	private String puuid;
	private String championName;
	private int championId;
	private int teamId;
	private boolean win;

	public boolean hasSamePuuid(final String puuid) {
		return this.puuid.equals(puuid);
	}
}
