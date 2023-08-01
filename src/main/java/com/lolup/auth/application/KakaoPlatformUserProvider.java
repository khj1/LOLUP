package com.lolup.auth.application;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.lolup.auth.application.dto.KaKaoPlatformUser;
import com.lolup.auth.application.dto.OAuthAccessTokenResponse;
import com.lolup.auth.application.dto.PlatformUserDto;

@Component
public class KakaoPlatformUserProvider {

	protected static final String ACCESS_TOKEN_URI = "/oauth/token";
	protected static final String USER_INFO_URI = "/v2/user/me";

	private final WebClient authorizationWebClient;
	private final WebClient resourceWebClient;
	private final String kakaoClientId;
	private final String kakaoClientSecret;

	public KakaoPlatformUserProvider(
			@Qualifier("kakaoAuthorizationWebClient") final WebClient authorizationWebClient,
			@Qualifier("kakaoResourceWebClient") final WebClient resourceWebClient,
			@Value("${spring.security.oauth2.client.registration.kakao.clientId}") final String kakaoClientId,
			@Value("${spring.security.oauth2.client.registration.kakao.clientSecret}") final String kakaoClientSecret) {
		this.authorizationWebClient = authorizationWebClient;
		this.resourceWebClient = resourceWebClient;
		this.kakaoClientId = kakaoClientId;
		this.kakaoClientSecret = kakaoClientSecret;
	}

	public PlatformUserDto getPlatformUser(final String code, final String redirectUri) {
		OAuthAccessTokenResponse accessTokenResponse = requestAccessToken(code, redirectUri);
		KaKaoPlatformUser kaKaoPlatformUser = requestKakaoPlatformUser(accessTokenResponse);
		return new PlatformUserDto(
				kaKaoPlatformUser.getNickname(),
				kaKaoPlatformUser.getEmail(),
				kaKaoPlatformUser.getProfileImage()
		);
	}

	private OAuthAccessTokenResponse requestAccessToken(final String code, final String redirectUri) {
		return authorizationWebClient.post()
				.uri(uriBuilder -> uriBuilder.path(ACCESS_TOKEN_URI)
						.queryParam("grant_type", "authorization_code")
						.queryParam("client_id", kakaoClientId)
						.queryParam("client_secret", kakaoClientSecret)
						.queryParam("redirect_uri", redirectUri)
						.queryParam("code", code)
						.build())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.retrieve()
				.bodyToMono(OAuthAccessTokenResponse.class)
				.block();
	}

	private KaKaoPlatformUser requestKakaoPlatformUser(final OAuthAccessTokenResponse accessTokenResponse) {
		return resourceWebClient.post()
				.uri(uriBuilder -> uriBuilder.path(USER_INFO_URI)
						.queryParam("secure_resource", true)
						.queryParam("property_keys", "[\"kakao_account.profile\", \"kakao_account.email\"]")
						.build())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessTokenResponse.getAccessToken()))
				.retrieve()
				.bodyToMono(KaKaoPlatformUser.class)
				.block();
	}
}
