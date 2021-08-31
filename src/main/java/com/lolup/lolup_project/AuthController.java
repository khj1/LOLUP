package com.lolup.lolup_project;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @DeleteMapping("/auth/logout")
    public ResponseEntity<Map<String, Object>> logout(Principal principal, HttpServletResponse response) {
        Map<String, Object> map = authService.logout(principal, response);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
