package com.lolup.config.oauth;

import com.lolup.member.domain.Member;
import com.lolup.member.domain.Role;
import com.lolup.member.domain.SocialType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserProfile {

	private final String name;
	private final String email;
	private final String picture;
	private final SocialType socialType;

	@Builder
	public UserProfile(final String name, final String email, final String picture, final SocialType socialType) {
		this.name = name;
		this.email = email;
		this.picture = picture;
		this.socialType = socialType;
	}

	public static UserProfile create(final Member member) {
		return UserProfile.builder()
				.name(member.getName())
				.email(member.getEmail())
				.picture(member.getPicture())
				.socialType(member.getSocialType())
				.build();
	}

	public Member toMember() {
		return new Member(name, email, Role.USER, picture, socialType);
	}
}
