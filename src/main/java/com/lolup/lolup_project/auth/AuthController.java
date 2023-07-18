package com.lolup.lolup_project.auth;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@GetMapping("/check")
	public ResponseEntity<Map<String, Object>> checkAuth(@AuthenticationPrincipal Long memberId) {
		Map<String, Object> map = authService.checkAuth(memberId);
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	@PostMapping("/refresh")
	public ResponseEntity<AccessTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
		AccessTokenResponse response = authService.refreshToken(request.getRefreshToken());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{memberId}")
	public ResponseEntity<Map<String, Object>> logout(@PathVariable Long memberId, HttpServletResponse response) {
		Map<String, Object> map = authService.logout(memberId, response);
		return new ResponseEntity<>(map, HttpStatus.OK);
	}
}
