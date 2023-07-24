package com.lolup.lolup_project.duo.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lolup.lolup_project.duo.application.dto.DuoDto;

public interface DuoRepositoryCustom {
	Page<DuoDto> findAll(final String position, final String tier, final Pageable pageable);
}
