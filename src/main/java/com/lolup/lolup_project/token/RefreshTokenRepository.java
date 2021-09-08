package com.lolup.lolup_project.token;

public interface RefreshTokenRepository {

    RefreshToken findByEmail(String email);

    String save(RefreshToken refreshToken);

    void delete(Long memberId);
}
