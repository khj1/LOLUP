package com.lolup.config.oauth;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import com.lolup.member.domain.SocialType;

public enum OAuthAttributes {

	GOOGLE("google", (attributes) -> {
		return new UserProfile(
				(String)attributes.get("name"),
				(String)attributes.get("email"),
				(String)attributes.get("picture"),
				SocialType.GOOGLE);
	}),

	@SuppressWarnings("unchecked") //DefaultOAuth2User의 attributes 자료형이 Map<String, Object>이므로 형 안정성을 유지한다.
	NAVER("naver", (attributes) -> {
		Map<String, Object> response = (Map<String, Object>)attributes.get("response");

		return new UserProfile(
				(String)response.get("name"),
				(String)response.get("email"),
				(String)response.get("picture"),
				SocialType.NAVER);
	}),

	@SuppressWarnings("unchecked")
	KAKAO("kakao", (attributes) -> {
		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
		Map<String, Object> kakaoProfile = (Map<String, Object>)kakaoAccount.get("profile");

		return new UserProfile(
				(String)kakaoProfile.get("nickname"),
				(String)kakaoAccount.get("email"),
				(String)kakaoProfile.get("profile_image_url"),
				SocialType.KAKAO);
	});

	private final String registrationId;
	private final Function<Map<String, Object>, UserProfile> of;

	OAuthAttributes(String registrationId, Function<Map<String, Object>, UserProfile> of) {
		this.registrationId = registrationId;
		this.of = of;
	}

	public static UserProfile extract(final String registrationId, final Map<String, Object> attributes) {
		return Arrays.stream(values())
				.filter(provider -> registrationId.equals(provider.registrationId))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new)
				.of.apply(attributes);
	}
}
