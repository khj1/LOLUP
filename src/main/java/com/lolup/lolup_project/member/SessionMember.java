package com.lolup.lolup_project.member;

import lombok.Getter;

import java.io.Serializable;

/**
 * 직렬화 기능을 가진 Member 클래스
 */
@Getter
public class SessionMember implements Serializable {
    private String name;
    private String email;
    private String picture;
    private String summonerName;

    public SessionMember(Member member) {
        this.name = member.getName();
        this.email = member.getEmail();
        this.picture = member.getPicture();
        this.summonerName = member.getSummonerName();
    }
}
