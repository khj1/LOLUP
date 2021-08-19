package com.lolup.lolup_project.duo;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MybatisDuoRepository extends DuoRepository{

    @Override
    List<DuoDto> findAll(ParameterDto parameterDto);

    @Override
    DuoDto findById(Long duoId);

    @Override
    Long save(DuoDto dto);

    @Override
    Long update(Long duoId, String position, String desc);

    @Override
    Long delete(Long duoId);
}
