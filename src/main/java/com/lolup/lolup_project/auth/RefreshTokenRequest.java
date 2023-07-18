package com.lolup.lolup_project.auth;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequest {

	@NotBlank(message = "리프레시 토큰은 공백일 수 없습니다.")
	private String refreshToken;

	private RefreshTokenRequest() {
	}

	public RefreshTokenRequest(final String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}
}
