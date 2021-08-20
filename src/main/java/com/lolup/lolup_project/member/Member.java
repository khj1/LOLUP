package com.lolup.lolup_project.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class Member {

    private Long memberId;
    private String oauthId;
    private String name;
    private String email;
    private String picture;
    private String summonerName;

    @NotNull
    private Role role;

    public Member(String oauthId, String name, String email, Role role, String picture, String summonerName) {
        this(null, oauthId, name, email, role, picture, null);
    }

    @Builder
    public Member(Long memberId, String oauthId, String name, String email, Role role, String picture, String summonerName) {
        this.memberId = memberId;
        this.oauthId = oauthId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.picture = picture;
        this.summonerName = summonerName;
    }

    public Member update(String name, String email, String picture) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
