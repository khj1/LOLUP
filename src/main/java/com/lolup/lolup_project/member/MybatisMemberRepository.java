package com.lolup.lolup_project.member;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Mapper
public interface MybatisMemberRepository extends MemberRepository{

    @Override
    Optional<Member> findByOauthId(String oauthId);

    @Override
    Optional<Member> findByEmail(@Param("_email") String email);

    @Override
    String save(Member memberDto);
}
