package com.lolup.lolup_project.duo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface DuoRepository {

    List<DuoDto> findAll(String position, String tier);

    DuoDto findById(Long duoId);

    Long save(DuoDto dto);

    Long update(Long duoId, String position, String desc);

    Long delete(Long duoId);
}
