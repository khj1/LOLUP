package com.lolup.auth.application.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlatformUserDto {

	private String name;
	private String email;
	private String picture;

	public PlatformUserDto(final String name, final String email, final String picture) {
		this.name = name;
		this.email = email;
		this.picture = picture;
	}
}
