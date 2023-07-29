package com.lolup.config.oauth;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.lolup.member.domain.Member;
import com.lolup.member.domain.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final MemberRepository memberRepository;

	@Override
	public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		Map<String, Object> attributes = oAuth2User.getAttributes();
		String userNameAttributeName = getUserNameAttributeName(userRequest);

		UserProfile userProfile = OAuthAttributes.extract(registrationId, attributes);
		saveMember(userProfile);

		return new DefaultOAuth2User(null, attributes, userNameAttributeName);
	}

	private String getUserNameAttributeName(final OAuth2UserRequest userRequest) {
		return userRequest
				.getClientRegistration()
				.getProviderDetails()
				.getUserInfoEndpoint()
				.getUserNameAttributeName();
	}

	private void saveMember(final UserProfile userProfile) {
		Optional<Member> findMember = memberRepository.findByEmailAndSocialType(
				userProfile.getEmail(),
				userProfile.getSocialType()
		);
		if (findMember.isEmpty()) {
			memberRepository.save(userProfile.toEntity());
		}
	}
}
