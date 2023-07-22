package com.lolup.lolup_project.duo.dto;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class DuoResponse {

	private String version;
	private Long totalCount;
	private List<DuoDto> content;
	private Pageable pageable;

	private DuoResponse() {
	}

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

	public String getVersion() {
		return version;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public List<DuoDto> getContent() {
		return content;
	}

	public Pageable getPageable() {
		return pageable;
	}
}
