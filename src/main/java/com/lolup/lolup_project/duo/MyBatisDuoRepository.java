package com.lolup.lolup_project.duo;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MyBatisDuoRepository implements DuoRepository{

    @Override
    public List<DuoDto> findAll(String tier, String position) {
        return null;
    }

    @Override
    public DuoDto findById(Long duoId) {
        return null;
    }

    @Override
    public Long save(DuoDto dto) {
        return null;
    }

    @Override
    public Long update(Long duoId, DuoDto dto) {
        return null;
    }

    @Override
    public Long delete(Long duoId) {
        return null;
    }
}
