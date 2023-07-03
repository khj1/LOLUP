package com.lolup.lolup_project.auth;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@GetMapping("/check")
	public ResponseEntity<Map<String, Object>> checkAuth(Principal principal) {
		Map<String, Object> map = authService.checkAuth(principal);
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	@GetMapping("/refresh")
	public ResponseEntity<Map<String, Object>> refresh(
			@CookieValue(value = "refreshToken", required = true) String refreshToken,
			HttpServletResponse response
	) {
		log.info("refreshToken={}", refreshToken);

		Map<String, Object> map = authService.refresh(refreshToken, response);
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	@DeleteMapping("/{memberId}")
	public ResponseEntity<Map<String, Object>> logout(@PathVariable Long memberId, HttpServletResponse response) {
		Map<String, Object> map = authService.logout(memberId, response);
		return new ResponseEntity<>(map, HttpStatus.OK);
	}
}
