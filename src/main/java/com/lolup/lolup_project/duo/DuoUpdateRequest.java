package com.lolup.lolup_project.duo;

import jakarta.validation.constraints.NotBlank;

public class DuoUpdateRequest {

	@NotBlank(message = "포지션은 공백일 수 없습니다.")
	private String position;

	@NotBlank(message = "설명란은 공백일 수 없습니다.")
	private String desc;

	private DuoUpdateRequest() {
	}

	public DuoUpdateRequest(final String position, final String desc) {
		this.position = position;
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	public String getPosition() {
		return position;
	}
}
