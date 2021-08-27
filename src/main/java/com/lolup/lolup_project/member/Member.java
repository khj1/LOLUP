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
    private String name;
    private String email;
    private String picture;
    private String summonerName;

    @NotNull
    private Role role;

    public Member(String name, String email, Role role, String picture, String summonerName) {
        this(null, name, email, role, picture, null);
    }

    @Builder
    public Member(Long memberId, String name, String email, Role role, String picture, String summonerName) {
        this.memberId = memberId;
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

    public UserProfile toUserProfile(String name, String email, String picture) {
        return new UserProfile(name, email, picture);
    }

    public static UserProfile toUserProfileWithMember(Member member) {
        return new UserProfile(
                member.getName(),
                member.getEmail(),
                member.getPicture()
        );
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
