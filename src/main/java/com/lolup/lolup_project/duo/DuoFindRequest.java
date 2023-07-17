package com.lolup.lolup_project.duo;

import org.springframework.data.domain.Pageable;

public class DuoFindRequest {

	private String position;
	private String tier;
	private Pageable pageable;

	private DuoFindRequest() {
	}

	public DuoFindRequest(final String position, final String tier, final Pageable pageable) {
		this.position = position;
		this.tier = tier;
		this.pageable = pageable;
	}

	public String getPosition() {
		return position;
	}

	public String getTier() {
		return tier;
	}

	public Pageable getPageable() {
		return pageable;
	}
}
