package com.lolup.lolup_project.auth.dto;

public class AccessTokenResponse {

	private String accessToken;

	private AccessTokenResponse() {
	}

	public AccessTokenResponse(final String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessToken() {
		return accessToken;
	}
}
