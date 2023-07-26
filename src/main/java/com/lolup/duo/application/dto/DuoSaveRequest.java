package com.lolup.duo.application.dto;

import com.lolup.duo.domain.SummonerPosition;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DuoSaveRequest {

	private String summonerName;
	private SummonerPosition position;
	private String desc;

	@Builder
	public DuoSaveRequest(final String summonerName, final SummonerPosition position, final String desc) {
		this.summonerName = summonerName;
		this.position = position;
		this.desc = desc;
	}
}
