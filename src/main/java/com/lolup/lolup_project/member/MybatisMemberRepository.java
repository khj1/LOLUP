package com.lolup.lolup_project.member;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Mapper
public interface MybatisMemberRepository extends MemberRepository{

    @Override
    Optional<Member> findByEmail(@Param("_email") String email);

    @Override
    String save(Member memberDto);

    @Override
    int update(@Param("_memberId") Long memberId,
               @Param("_summonerName") String summonerName);
}
