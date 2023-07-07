package com.lolup.lolup_project.token;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.lolup.lolup_project.auth.InvalidTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtProvider {

	private final SecretKey secretKey;
	private final long accessTokenValidityInMilliseconds;
	private final long refreshTokenValidityInMilliseconds;

	public JwtProvider(@Value("${security.jwt.token.secret-key}") final java.lang.String secretKey,
					   @Value("${security.jwt.token.access.expire-length}") final long accessTokenValidityInMilliseconds,
					   @Value("${security.jwt.token.refresh.expire-length}") final long refreshTokenValidityInMilliseconds) {
		this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
		this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
		this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
	}

	public String createAccessToken(final String payload) {
		return createToken(payload, accessTokenValidityInMilliseconds);
	}

	public String createRefreshToken(final String payload) {
		return createToken(payload, refreshTokenValidityInMilliseconds);
	}

	private String createToken(final String payload, final long validityInMilliseconds) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);

		return Jwts.builder()
				.setSubject(payload)
				.setIssuedAt(now)
				.setExpiration(validity)
				.signWith(secretKey, SignatureAlgorithm.HS256)
				.compact();
	}

	public java.lang.String getTokenClaims(final java.lang.String token) {
		return parseClaims(token)
				.getBody()
				.getSubject();
	}

	public void verifyToken(final String token) {
		try {
			parseClaims(token);
		} catch (final MalformedJwtException e) {
			throw new InvalidTokenException("유효하지 않은 토큰 형식입니다.");
		} catch (final SignatureException e) {
			throw new InvalidTokenException("권한이 없습니다.");
		} catch (final ExpiredJwtException e) {
			throw new InvalidTokenException("토큰 기한이 만료되었습니다.");
		}
	}

	private Jws<Claims> parseClaims(final java.lang.String token) {
		return Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token);
	}
}
