package com.lolup.auth.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lolup.auth.application.AuthService;
import com.lolup.auth.application.dto.AccessTokenResponse;
import com.lolup.auth.presentation.dto.RefreshTokenDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

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
