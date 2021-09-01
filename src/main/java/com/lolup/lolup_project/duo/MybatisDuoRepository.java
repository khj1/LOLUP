package com.lolup.lolup_project.duo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MybatisDuoRepository extends DuoRepository{

    @Override
    List<DuoDto> findAll(@Param("_position") String position,
                         @Param("_tier") String tier,
                         @Param("_section") int section);

    @Override
    DuoDto findById(Long duoId);

    @Override
    Integer getTotalCount();

    @Override
    Long save(DuoDto dto);

    @Override
    Long update(Long duoId, String position, String desc);

    @Override
    Long delete(Long duoId);
}
