package com.lolup.lolup_project.member.dto;

import jakarta.validation.constraints.NotBlank;

public class MemberUpdateRequest {

	@NotBlank(message = "소환사 이름은 공백일 수 없습니다.")
	private String summonerName;

	private MemberUpdateRequest() {
	}

	public MemberUpdateRequest(final String summonerName) {
		this.summonerName = summonerName;
	}

	public String getSummonerName() {
		return summonerName;
	}
}
