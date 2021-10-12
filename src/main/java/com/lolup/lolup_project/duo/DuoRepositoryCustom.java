package com.lolup.lolup_project.duo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DuoRepositoryCustom {
    Page<DuoDto> findAll(String position, String tier, Pageable pageable);

    void update(Long duoId, String position, String desc);

    void delete(Long duoId, Long memberId);
}
