package com.lolup.lolup_project.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
		AccessTokenResponse response = authService.refreshToken(refreshRequest.getRefreshToken());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@Valid @RequestBody final RefreshTokenDto refreshTokenDto) {
		authService.logout(refreshTokenDto.getRefreshToken());
		return ResponseEntity.noContent().build();
	}
}
