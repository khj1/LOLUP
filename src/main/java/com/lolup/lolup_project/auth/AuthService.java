package com.lolup.lolup_project.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lolup.lolup_project.member.Member;
import com.lolup.lolup_project.member.MemberRepository;
import com.lolup.lolup_project.token.JwtTokenProvider;
import com.lolup.lolup_project.token.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

	private final JwtTokenProvider jwtTokenProvider;
	private final MemberRepository memberRepository;
	private final RefreshTokenRepository refreshTokenRepository;

	public Map<String, Object> checkAuth(Long memberId) {
		Member findMember = memberRepository.findById(memberId)
				.orElseThrow(IllegalArgumentException::new);

		Map<String, Object> map = new HashMap<>();
		map.put("memberId", findMember.getId());
		map.put("summonerName", findMember.getSummonerName());
		map.put("login", true);

		return map;
	}

	@Transactional
	public AccessTokenResponse refreshToken(String refreshToken) {
		verifyRefreshToken(refreshToken);

		String memberId = jwtTokenProvider.getPayload(refreshToken);
		String accessToken = jwtTokenProvider.createAccessToken(memberId);

		return new AccessTokenResponse(accessToken);
	}

	private void verifyRefreshToken(final String refreshToken) {
		refreshTokenRepository.findByRefreshToken(refreshToken)
				.orElseThrow(NoSuchRefreshTokenException::new);

		jwtTokenProvider.verifyToken(refreshToken);
	}

	@Transactional
	public void logout(final String refreshToken) {
		refreshTokenRepository.deleteByRefreshToken(refreshToken);
	}
}
