package com.lolup.lolup_project.config.oauth;

import com.lolup.lolup_project.member.domain.Member;
import com.lolup.lolup_project.member.domain.Role;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserProfile {
	private final String name;
	private final String email;
	private final String picture;

	@Builder
	public UserProfile(String name, String email, String picture) {
		this.name = name;
		this.email = email;
		this.picture = picture;
	}

	public static UserProfile create(final Member member) {
		return UserProfile.builder()
				.name(member.getName())
				.email(member.getEmail())
				.picture(member.getPicture())
				.build();
	}

	public Member toMember() {
		return new Member(name, email, Role.USER, picture);
	}
}
