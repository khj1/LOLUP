package com.lolup.lolup_project.duo.application.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DuoSaveRequest {

	private String summonerName;
	private String position;
	private String desc;

	@Builder
	public DuoSaveRequest(final String summonerName, final String position, final String desc) {
		this.summonerName = summonerName;
		this.position = position;
		this.desc = desc;
	}
}
