package com.lolup.lolup_project.duo.application.dto;

import com.lolup.lolup_project.riot.summoner.domain.MostInfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MostInfoDto {

	private String name;
	private Long play;

	public MostInfoDto(final String name, final Long play) {
		this.name = name;
		this.play = play;
	}

	public static MostInfoDto create(final MostInfo mostInfo) {
		return new MostInfoDto(mostInfo.getName(), mostInfo.getPlay());
	}
}
