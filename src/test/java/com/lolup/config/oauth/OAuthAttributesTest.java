package com.lolup.config.oauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.lolup.config.oauth.exception.NoSuchSocialPlatformException;
import com.lolup.member.domain.SocialType;

class OAuthAttributesTest {

	public static final String KAKAO_NAME = "kakaoNickname";
	public static final String KAKAO_EMAIL = "aaa@bbb.ccc";
	public static final String KAKAO_PROFILE_IMAGE = "kakaoProfileImage";

	public static final String GOOGLE_NAME = "googleNickname";
	public static final String GOOGLE_EMAIL = "aaa@bbb.ccc";
	public static final String GOOGLE_PROFILE_IMAGE = "googleProfileImage";

	public static final String NAVER_NAME = "naverleNickname";
	public static final String NAVER_EMAIL = "aaa@bbb.ccc";
	public static final String NAVER_PROFILE_IMAGE = "naverProfileImage";

	public static Stream<Arguments> generateAttributesByRegistrationId() {
		return Stream.of(
				Arguments.of("kakao", createKakaoAttributes(), createKakaoProfile()),
				Arguments.of("google", createGoogleAttributes(), createGoogleProfile()),
				Arguments.of("naver", createNaverAttributes(), createNaverProfile())
		);
	}

	private static Map<String, Object> createKakaoAttributes() {
		HashMap<String, Object> kakaoProfile = new HashMap<>();
		kakaoProfile.put("nickname", KAKAO_NAME);
		kakaoProfile.put("profile_image_url", KAKAO_PROFILE_IMAGE);

		HashMap<String, Object> kakaoAccount = new HashMap<>();
		kakaoAccount.put("profile", kakaoProfile);
		kakaoAccount.put("email", KAKAO_EMAIL);

		HashMap<String, Object> kakaoAttributes = new HashMap<>();
		kakaoAttributes.put("kakao_account", kakaoAccount);

		return kakaoAttributes;
	}

	private static Map<String, Object> createGoogleAttributes() {
		HashMap<String, Object> googleAttributes = new HashMap<>();
		googleAttributes.put("name", GOOGLE_NAME);
		googleAttributes.put("email", GOOGLE_EMAIL);
		googleAttributes.put("picture", GOOGLE_PROFILE_IMAGE);

		return googleAttributes;
	}

	private static Map<String, Object> createNaverAttributes() {
		HashMap<String, Object> response = new HashMap<>();
		response.put("name", NAVER_NAME);
		response.put("email", NAVER_EMAIL);
		response.put("profile_image", NAVER_PROFILE_IMAGE);

		HashMap<String, Object> naverAttributes = new HashMap<>();
		naverAttributes.put("response", response);

		return naverAttributes;
	}

	private static UserProfile createKakaoProfile() {
		return new UserProfile(KAKAO_NAME, KAKAO_EMAIL, KAKAO_PROFILE_IMAGE, SocialType.KAKAO);
	}

	private static UserProfile createGoogleProfile() {
		return new UserProfile(GOOGLE_NAME, GOOGLE_EMAIL, GOOGLE_PROFILE_IMAGE, SocialType.GOOGLE);
	}

	private static UserProfile createNaverProfile() {
		return new UserProfile(NAVER_NAME, NAVER_EMAIL, NAVER_PROFILE_IMAGE, SocialType.NAVER);
	}

	@DisplayName("소셜 로그인 플랫폼 타입 별로 유저 정보를 추출할 수 있다.")
	@ParameterizedTest
	@MethodSource("generateAttributesByRegistrationId")
	void extract(final String registrationId, final Map<String, Object> attributes, final UserProfile expected) {
		UserProfile userProfile = OAuthAttributes.extract(registrationId, attributes);

		assertThat(userProfile)
				.usingRecursiveComparison()
				.isEqualTo(expected);
	}

	@DisplayName("지원하지 않는 소셜 로그인 플랫폼 타입이 입력되면 예외가 발생한다.")
	@Test
	void test() {
		assertThatThrownBy(() -> OAuthAttributes.extract("github", Map.of()))
				.isInstanceOf(NoSuchSocialPlatformException.class);
	}
}
