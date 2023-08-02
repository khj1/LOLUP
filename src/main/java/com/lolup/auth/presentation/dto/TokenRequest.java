package com.lolup.auth.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenRequest {

	@NotNull(message = "인가 코드는 공백일 수 없습니다.")
	private String code;

	@NotNull(message = "reidrectUri는 공백일 수 없습니다.")
	private String redirectUri;
}
