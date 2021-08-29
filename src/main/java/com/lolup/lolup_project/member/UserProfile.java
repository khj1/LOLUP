package com.lolup.lolup_project.member;

import lombok.Getter;


@Getter
public class UserProfile {
    private final String name;
    private final String email;
    private final String picture;

    public UserProfile(String name, String email, String picture) {
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    public Member toMember() {
        return new Member(name, email, Role.USER, picture, null);
    }
}
