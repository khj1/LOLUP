package com.lolup.lolup_project.duo.application.dto;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DuoResponse {

	private String version;
	private Long totalCount;
	private List<DuoDto> content;
	private Pageable pageable;

	public DuoResponse(final Page<DuoDto> data, final String gameVersion) {
		this(gameVersion, data.getTotalElements(), data.getContent(), data.getPageable());
	}

	public DuoResponse(final String version, final Long totalCount, final List<DuoDto> content,
					   final Pageable pageable) {
		this.version = version;
		this.totalCount = totalCount;
		this.content = content;
		this.pageable = pageable;
	}
}
