package com.lolup.lolup_project.duo.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lolup.lolup_project.duo.dto.DuoDto;

public interface DuoRepositoryCustom {
	Page<DuoDto> findAll(String position, String tier, Pageable pageable);
}
