package com.lolup.auth.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthAccessTokenResponse {

	private String access_token;

	public String getAccessToken() {
		return access_token;
	}
}
