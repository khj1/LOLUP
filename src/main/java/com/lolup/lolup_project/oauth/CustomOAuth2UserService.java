package com.lolup.lolup_project.oauth;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.lolup.lolup_project.member.domain.Member;
import com.lolup.lolup_project.member.domain.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final MemberRepository memberRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		Map<String, Object> attributes = oAuth2User.getAttributes();
		String userNameAttributeName = getUserNameAttributeName(userRequest);

		UserProfile userProfile = OAuthAttributes.extract(registrationId, attributes);
		Member member = saveOrUpdate(userProfile);

		return new DefaultOAuth2User(
				Collections.singleton(new SimpleGrantedAuthority(member.getRoleKey())),
				attributes,
				userNameAttributeName
		);
	}

	private String getUserNameAttributeName(final OAuth2UserRequest userRequest) {
		return userRequest
				.getClientRegistration()
				.getProviderDetails()
				.getUserInfoEndpoint()
				.getUserNameAttributeName();
	}

	private Member saveOrUpdate(UserProfile userProfile) {
		Member member = memberRepository.findByEmail(userProfile.getEmail()).orElse(null);

		if (member == null) {
			member = userProfile.toMember();
			member = memberRepository.save(member);
		} else {
			member.update(userProfile.getName(), userProfile.getEmail(), userProfile.getPicture());
		}
		return member;
	}
}
