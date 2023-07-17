package com.lolup.lolup_project.duo;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DuoSaveRequest {
	private String summonerName;
	private String position;
	private String desc;
	private LocalDateTime postDate;

	private DuoSaveRequest() {
	}

	@Builder
	public DuoSaveRequest(String summonerName, String position, String desc, LocalDateTime postDate) {
		this.summonerName = summonerName;
		this.position = position;
		this.desc = desc;
		this.postDate = postDate;
	}
}
