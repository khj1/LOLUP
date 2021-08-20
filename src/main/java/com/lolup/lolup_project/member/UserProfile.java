package com.lolup.lolup_project.member;

import lombok.Getter;

@Getter
public class UserProfile {
    private final String oauthId;
    private final String name;
    private final String email;
    private final String picture;

    public UserProfile(String oauthId, String name, String email, String picture) {
        this.oauthId = oauthId;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    public Member toMember() {
        return new Member(oauthId, name, email, Role.USER, picture, null);
    }
}
