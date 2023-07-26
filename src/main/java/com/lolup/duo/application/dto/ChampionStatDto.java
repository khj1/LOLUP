package com.lolup.duo.application.dto;

import com.lolup.riot.summoner.domain.ChampionStat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChampionStatDto {

	private String name;
	private Long play;

	public ChampionStatDto(final String name, final Long play) {
		this.name = name;
		this.play = play;
	}

	public static ChampionStatDto create(final ChampionStat mostInfo) {
		return new ChampionStatDto(mostInfo.getName(), mostInfo.getPlay());
	}
}
