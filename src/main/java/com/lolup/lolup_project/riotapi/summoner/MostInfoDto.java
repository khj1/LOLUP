package com.lolup.lolup_project.riotapi.summoner;

import lombok.Data;

@Data
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
