package com.lolup.lolup_project.member;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

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

    public static UserProfile toDto(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
        return new UserProfile(
                (String) kakaoProfile.get("nickname"),
                (String) kakaoAccount.get("email"),
                (String) kakaoProfile.get("profile_image_url")
        );
    }

    public Member toMember() {
        return new Member(name, email, Role.USER, picture, null);
    }
}
