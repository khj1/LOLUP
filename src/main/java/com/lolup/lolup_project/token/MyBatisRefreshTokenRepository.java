package com.lolup.lolup_project.token;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface MyBatisRefreshTokenRepository extends RefreshTokenRepository{

    @Override
    RefreshToken findByEmail(@Param("_email") String email);

    @Override
    String save(RefreshToken refreshToken);

    @Override
    void delete(@Param("_memberId") Long memberId);
}
