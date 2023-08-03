package com.lolup.duo.application.dto;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DuoResponse {

	private String version;
	private Long totalCount;
	private List<DuoDto> content;
	private Pageable pageable;

	public DuoResponse(final Page<DuoDto> data, final String gameVersion) {
		this(gameVersion, data.getTotalElements(), data.getContent(), data.getPageable());
	}
}
