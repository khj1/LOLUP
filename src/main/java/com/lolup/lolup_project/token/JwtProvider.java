package com.lolup.lolup_project.token;

import com.lolup.lolup_project.token.Token;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private long TOKEN_VALIDATION_PERIOD = 1000L * 30 * 60; // 30분
    private long REFRESH_TOKEN_VALIDATION_PERIOD = 1000L * 60L * 60L * 24L * 30L; // 한달

    final static public String ACCESS_TOKEN_NAME = "accessToken";
    final static public String REFRESH_TOKEN_NAME = "refreshToken";

    @PostConstruct
    protected void init() {
        // secretKey를 Base64로 인코딩한다.
        SECRET_KEY = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
    }

    // 토큰 생성
    public Token generateToken(String email, String role) {
        Claims claims = Jwts.claims().setSubject(email); // JWT payLoad에 저장되는 정보단위
        claims.put("role", role);

        log.info("generateToken() 호출");

        Date now = new Date();
        return new Token(
                Jwts.builder()
                        .setClaims(claims) // 정보 저장
                        .setIssuedAt(now) // 토큰 발행 시간 정보
                        .setExpiration(new Date(now.getTime() + TOKEN_VALIDATION_PERIOD))
                        .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 사용할 암호화 알고리즘과 Signature에 들어갈 secret 값 세팅
                        .compact(),

                Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALIDATION_PERIOD))
                        .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                        .compact()
        );
    }

    public boolean verifyToken(String token) throws ExpiredJwtException {
        log.info("verifyToken() 호출");
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .after(new Date());
    }

    public String getTokenClaims(String token) {
        log.info("getTokenClaims() 호출");
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Request의 Header에서 token 값을 가져옵니다. "Authorization" : "Bearer TOKEN값'
    public String resolveToken(HttpServletRequest request, String type) {
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.toLowerCase().startsWith(type.toLowerCase())) {
            return authorization.substring(type.length()).trim();
        }

        return null;
    }
}
