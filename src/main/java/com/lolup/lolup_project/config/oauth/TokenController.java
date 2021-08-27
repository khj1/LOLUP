package com.lolup.lolup_project.config.oauth;

import com.lolup.lolup_project.member.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
public class TokenController {
    private final JwtProvider jwtProvider;

    @GetMapping("/token/expired")
    public String auth() {
        throw new RuntimeException("토큰이 만료되었습니다.");
    }

    @GetMapping("/token/refresh")
    public String refreshAuth(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Refresh");

        if (token != null && jwtProvider.verifyToken(token)) {
            String email = jwtProvider.getTokenClaims(token);
            Token newToken = jwtProvider.generateToken(email, Role.USER.getKey());

            response.addHeader("X-AUTH-TOKEN", newToken.getToken());
            response.addHeader("X-REFRESH-TOKEN", newToken.getRefreshToken());
            response.setContentType("application/json;charset=UTF-8");

            return "new token issued";
        }

        throw new RuntimeException();
    }
}
