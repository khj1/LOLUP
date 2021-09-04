package com.lolup.lolup_project;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/auth/check")
    public ResponseEntity<Map<String, Object>> checkAuth(Principal principal) {
        Map<String, Object> map = authService.checkAuth(principal);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/auth/refresh")
    public ResponseEntity<Map<String, Object>> refresh(
            @CookieValue(value = "refreshToken", required = true) String refreshToken,
            HttpServletResponse response
    ) {
        log.info("refreshToken={}", refreshToken);

        Map<String, Object> map = authService.refresh(refreshToken, response);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @DeleteMapping("/auth/{memberId}")
    public ResponseEntity<Map<String, Object>> logout(@PathVariable Long memberId, HttpServletResponse response) {
        Map<String, Object> map = authService.logout(memberId, response);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
