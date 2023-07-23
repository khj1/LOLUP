package com.lolup.lolup_project.duo.application.dto;

import java.time.LocalDateTime;

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
	private LocalDateTime postDate;

	@Builder
	public DuoSaveRequest(final String summonerName, final String position, final String desc,
						  final LocalDateTime postDate) {
		this.summonerName = summonerName;
		this.position = position;
		this.desc = desc;
		this.postDate = postDate;
	}
}
