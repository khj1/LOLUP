package com.lolup.lolup_project.member.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberUpdateRequest {

	@NotBlank(message = "소환사 이름은 공백일 수 없습니다.")
	private String summonerName;

	public MemberUpdateRequest(final String summonerName) {
		this.summonerName = summonerName;
	}
}
