package com.lolup.lolup_project.duo;

import java.util.List;

public interface DuoRepository {

    List<DuoDto> findAll(String tier, String position);

    DuoDto findById(Long duoId);

    Long save(DuoDto dto);

    Long update(Long duoId, DuoDto form);

    Long delete(Long duoId);
}
