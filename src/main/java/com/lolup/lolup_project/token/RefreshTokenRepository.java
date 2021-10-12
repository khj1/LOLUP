package com.lolup.lolup_project.token;

import com.lolup.lolup_project.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    void deleteByMember(Member member);
}
