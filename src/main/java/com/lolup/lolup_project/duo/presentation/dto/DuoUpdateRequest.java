package com.lolup.lolup_project.duo.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DuoUpdateRequest {

	@NotBlank(message = "포지션은 공백일 수 없습니다.")
	private String position;

	@NotBlank(message = "설명란은 공백일 수 없습니다.")
	private String desc;

	public DuoUpdateRequest(final String position, final String desc) {
		this.position = position;
		this.desc = desc;
	}
}
