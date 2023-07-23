package com.lolup.lolup_project.duo.presentation.dto;

import org.springframework.data.domain.Pageable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DuoFindRequest {

	private String position;
	private String tier;
	private Pageable pageable;

	public DuoFindRequest(final String position, final String tier, final Pageable pageable) {
		this.position = position;
		this.tier = tier;
		this.pageable = pageable;
	}
}
