package com.lolup.auth.application;

import org.springframework.stereotype.Component;

import com.lolup.auth.application.dto.PlatformUserDto;

@Component
public class KakaoPlatformUserProvider {

	public PlatformUserDto getPlatformUser(final String code, final String redirectUri) {
		return new PlatformUserDto("", "", "");
	}
}
