package com.lolup.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
	
	private String code;
	private String message;

	public ErrorResponse(final String code, final String message) {
		this.code = code;
		this.message = message;
	}
}
