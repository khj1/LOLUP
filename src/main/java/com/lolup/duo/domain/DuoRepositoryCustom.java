package com.lolup.duo.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lolup.duo.application.dto.DuoDto;

public interface DuoRepositoryCustom {
	Page<DuoDto> findAll(final SummonerPosition position, final SummonerTier tier, final Pageable pageable);
}
