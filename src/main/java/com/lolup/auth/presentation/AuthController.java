package com.lolup.auth.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lolup.auth.application.AuthService;
import com.lolup.auth.application.dto.AccessTokenResponse;
import com.lolup.auth.application.dto.TokenResponse;
import com.lolup.auth.presentation.dto.RefreshTokenDto;
import com.lolup.auth.presentation.dto.TokenRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login/kakao")
	public ResponseEntity<TokenResponse> loginWithKakao(final TokenRequest tokenRequest) {
		TokenResponse tokenResponse = authService.createTokenWithKakaoOAuth(
				tokenRequest.getCode(),
				tokenRequest.getRedirectUri()
		);
		return ResponseEntity.ok().body(tokenResponse);
	}

	@PostMapping("/login/google")
	public ResponseEntity<TokenResponse> loginWithGoogle(final TokenRequest tokenRequest) {
		TokenResponse tokenResponse = authService.createTokenWithGoogleOAuth(
				tokenRequest.getCode(),
				tokenRequest.getRedirectUri()
		);
		return ResponseEntity.ok().body(tokenResponse);
	}

	@GetMapping("/check")
	public ResponseEntity<Void> checkAuthorization(@AuthenticationPrincipal final Long memberId) {
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/refresh")
	public ResponseEntity<AccessTokenResponse> refreshToken(@Valid @RequestBody final RefreshTokenDto refreshRequest) {
		AccessTokenResponse accessTokenResponse = authService.refreshToken(refreshRequest.getRefreshToken());
		return ResponseEntity.ok().body(accessTokenResponse);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@Valid @RequestBody final RefreshTokenDto refreshTokenDto) {
		authService.logout(refreshTokenDto.getRefreshToken());
		return ResponseEntity.noContent().build();
	}
}
