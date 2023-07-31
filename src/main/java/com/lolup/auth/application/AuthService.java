package com.lolup.auth.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lolup.auth.application.dto.AccessTokenResponse;
import com.lolup.auth.application.dto.PlatformUserDto;
import com.lolup.auth.application.dto.TokenResponse;
import com.lolup.auth.domain.RefreshTokenRepository;
import com.lolup.auth.exception.NoSuchRefreshTokenException;
import com.lolup.member.domain.Member;
import com.lolup.member.domain.MemberRepository;
import com.lolup.member.domain.SocialType;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

	private final MemberRepository memberRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final KakaoPlatformUserProvider kakaoPlatformUserProvider;

	public TokenResponse createTokenWithKakaoOAuth(final String code, final String redirectUri) {
		PlatformUserDto platformUser = kakaoPlatformUserProvider.getPlatformUser(code, redirectUri);
		Long memberId = findOrCreateMemberId(platformUser, SocialType.KAKAO);

		String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(memberId));
		String refreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(memberId));

		return new TokenResponse(memberId, accessToken, refreshToken);
	}

	private Long findOrCreateMemberId(final PlatformUserDto platformUser, final SocialType socialType) {
		return memberRepository.findByEmailAndSocialType(platformUser.getEmail(), socialType)
				.orElseGet(() -> saveMember(platformUser, socialType))
				.getId();
	}

	private Member saveMember(final PlatformUserDto platformUser, final SocialType socialType) {
		Member member = new Member(
				platformUser.getName(),
				platformUser.getEmail(),
				platformUser.getPicture(),
				socialType
		);
		return memberRepository.save(member);
	}

	public AccessTokenResponse refreshToken(final String refreshToken) {
		verifyRefreshToken(refreshToken);
		String memberId = jwtTokenProvider.getPayload(refreshToken);
		String accessToken = jwtTokenProvider.createAccessToken(memberId);

		return new AccessTokenResponse(accessToken);
	}

	private void verifyRefreshToken(final String refreshToken) {
		refreshTokenRepository.findByTokenValue(refreshToken)
				.orElseThrow(NoSuchRefreshTokenException::new);

		jwtTokenProvider.verifyToken(refreshToken);
	}

	public void logout(final String refreshToken) {
		refreshTokenRepository.deleteByTokenValue(refreshToken);
	}
}
