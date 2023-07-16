package com.lolup.lolup_project.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lolup.lolup_project.member.Member;
import com.lolup.lolup_project.member.MemberRepository;
import com.lolup.lolup_project.member.UserProfile;
import com.lolup.lolup_project.token.JwtProvider;
import com.lolup.lolup_project.token.RefreshToken;
import com.lolup.lolup_project.token.RefreshTokenRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

	private final JwtProvider jwtProvider;
	private final MemberRepository memberRepository;
	private final RefreshTokenRepository refreshTokenRepository;

	public Map<String, Object> checkAuth(Authentication authentication) {
		UserProfile userProfile = (UserProfile)(authentication.getPrincipal());
		Member findMember = memberRepository.findByEmail(userProfile.getEmail())
				.orElseThrow(IllegalArgumentException::new);

		Map<String, Object> map = new HashMap<>();
		map.put("memberId", findMember.getId());
		map.put("summonerName", findMember.getSummonerName());
		map.put("login", true);

		return map;
	}

	public Map<String, Object> refresh(String refreshToken, HttpServletResponse response) {
		jwtProvider.verifyToken(refreshToken);

		String email = jwtProvider.getTokenClaims(refreshToken);
		String newAccessToken = jwtProvider.createAccessToken(email);
		String newRefreshToken = jwtProvider.createRefreshToken(email);
		Member findMember = memberRepository.findByEmail(email)
				.orElseThrow(IllegalArgumentException::new);

		RefreshToken savedRefreshToken = refreshTokenRepository.save(RefreshToken.create(findMember, newRefreshToken));

		setCookie(response, savedRefreshToken.getRefreshToken());

		Map<String, Object> map = new HashMap<>();
		map.put("token", newAccessToken);

		return map;
	}

	public Map<String, Object> logout(Long memberId, HttpServletResponse response) {
		Member findMember = memberRepository.findById(memberId).orElse(null);
		refreshTokenRepository.deleteByMember(findMember);
		deleteCookie(response);

		Map<String, Object> map = new HashMap<>();
		map.put("logout", true);

		return map;
	}

	private void setCookie(HttpServletResponse response, String refreshToken) {
		Cookie cookie = new Cookie("refreshToken", refreshToken);
		cookie.setMaxAge(60 * 60 * 24 * 30); // 1달
		cookie.setSecure(false);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	private void deleteCookie(HttpServletResponse response) {
		Cookie cookie = new Cookie("refreshToken", null);
		cookie.setMaxAge(0);
		cookie.setSecure(false);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);
	}
}
