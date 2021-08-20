package com.lolup.lolup_project.member;


import java.util.Optional;

public interface MemberRepository{

    Optional<Member> findByOauthId(String oauthId);

    Optional<Member> findByEmail(String email);

    String save(Member memberDto);
}
