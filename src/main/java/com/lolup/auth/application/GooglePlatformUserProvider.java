package com.lolup.auth.application;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.lolup.auth.application.dto.GooglePlatformUser;
import com.lolup.auth.application.dto.OAuthAccessTokenResponse;
import com.lolup.auth.application.dto.PlatformUserDto;
import com.lolup.auth.exception.GoogleAuthorizationException;
import com.lolup.auth.exception.GoogleResourceException;

@Component
public class GooglePlatformUserProvider {

	private static final String ACCESS_TOKEN_URI = "/token";
	private static final String USER_INFO_URI = "/oauth2/v1/userinfo";

	private final WebClient authorizationWebClient;
	private final WebClient resourceWebClient;
	private final String googleClientId;
	private final String googleClientSecret;

	public GooglePlatformUserProvider(
			@Qualifier("googleAuthorizationWebClient") final WebClient authorizationWebClient,
			@Qualifier("googleResourceWebClient") final WebClient resourceWebClient,
			@Value("${spring.security.oauth2.client.registration.google.client-id}") final String googleClientId,
			@Value("${spring.security.oauth2.client.registration.google.client-secret}") final String googleClientSecret) {
		this.authorizationWebClient = authorizationWebClient;
		this.resourceWebClient = resourceWebClient;
		this.googleClientId = googleClientId;
		this.googleClientSecret = googleClientSecret;
	}

	public PlatformUserDto getPlatformUser(final String code, final String redirectUri) {
		OAuthAccessTokenResponse accessTokenResponse = requestAccessToken(code, redirectUri);
		GooglePlatformUser googlePlatformUser = requestPlatformUser(accessTokenResponse.getAccessToken());
		return new PlatformUserDto(
				googlePlatformUser.getName(),
				googlePlatformUser.getEmail(),
				googlePlatformUser.getPicture()
		);
	}

	private OAuthAccessTokenResponse requestAccessToken(final String code, final String redirectUri) {
		try {
			return authorizationWebClient.post()
					.uri(uriBuilder -> uriBuilder.path(ACCESS_TOKEN_URI)
							.queryParam("grant_type", "authorization_code")
							.queryParam("client_id", googleClientId)
							.queryParam("client_secret", googleClientSecret)
							.queryParam("redirect_uri", redirectUri)
							.queryParam("code", code)
							.build())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.retrieve()
					.bodyToMono(OAuthAccessTokenResponse.class)
					.block();
		} catch (RuntimeException e) {
			throw new GoogleAuthorizationException();
		}
	}

	private GooglePlatformUser requestPlatformUser(final String accessToken) {
		try {
			return resourceWebClient.get()
					.uri(uriBuilder -> uriBuilder.path(USER_INFO_URI)
							.queryParam("access_token", accessToken)
							.build())
					.retrieve()
					.bodyToMono(GooglePlatformUser.class)
					.block();
		} catch (RuntimeException e) {
			throw new GoogleResourceException();
		}
	}
}
