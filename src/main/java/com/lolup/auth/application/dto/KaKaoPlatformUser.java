package com.lolup.auth.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KaKaoPlatformUser {

	private KakaoAccount kakao_account;

	public String getEmail() {
		return kakao_account.getEmail();
	}

	public String getNickname() {
		return kakao_account.getProfile()
				.getNickname();
	}

	public String getProfileImage() {
		return kakao_account.getProfile()
				.getProfile_image_url();
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	private static class KakaoAccount {

		private String email;
		private KakaoProfile profile;

	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	private static class KakaoProfile {

		private String nickname;
		private String profile_image_url;
	}
}
