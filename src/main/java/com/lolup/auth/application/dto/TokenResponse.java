package com.lolup.auth.application.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenResponse {

	private Long memberId;
	private String accessToken;
	private String refreshToken;

	public TokenResponse(final Long memberId, final String accessToken, final String refreshToken) {
		this.memberId = memberId;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
}
