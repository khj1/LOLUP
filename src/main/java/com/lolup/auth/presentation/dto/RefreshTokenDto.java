package com.lolup.auth.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenDto {

	@NotBlank(message = "리프레시 토큰은 공백일 수 없습니다.")
	private String refreshToken;

	private RefreshTokenDto() {
	}

	public RefreshTokenDto(final String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}
}
