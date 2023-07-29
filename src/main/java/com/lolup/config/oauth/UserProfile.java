package com.lolup.config.oauth;

import com.lolup.member.domain.Member;
import com.lolup.member.domain.SocialType;

import lombok.Getter;

@Getter
public class UserProfile {

	private final String name;
	private final String email;
	private final String picture;
	private final SocialType socialType;

	public UserProfile(final String name, final String email, final String picture, final SocialType socialType) {
		this.name = name;
		this.email = email;
		this.picture = picture;
		this.socialType = socialType;
	}

	public static UserProfile create(final Member member) {
		return new UserProfile(
				member.getName(),
				member.getEmail(),
				member.getPicture(),
				member.getSocialType()
		);
	}

	public Member toEntity() {
		return new Member(name, email, picture, socialType);
	}
}
