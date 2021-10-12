package com.lolup.lolup_project.duo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface DuoRepository extends JpaRepository<Duo, Long>, DuoRepositoryCustom {
    void deleteByIdAndMemberId(Long duoId, Long memberId);
}
