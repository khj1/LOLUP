package com.lolup.duo.application.dto;

import com.lolup.riot.summoner.domain.ChampionStat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChampionStatDto {

	private String name;
	private Long count;

	public static ChampionStatDto create(final ChampionStat championStat) {
		return new ChampionStatDto(championStat.getName(), championStat.getCount());
	}

	public static ChampionStatDto create(final String name, final Long count) {
		return new ChampionStatDto(name, count);
	}

	public ChampionStat toEntity() {
		return new ChampionStat(name, count);
	}
}
