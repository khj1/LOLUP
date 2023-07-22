package com.lolup.lolup_project.auth.application;

import org.springframework.http.HttpHeaders;

import com.lolup.lolup_project.auth.exception.EmptyAuthorizationHeaderException;
import com.lolup.lolup_project.auth.exception.InvalidTokenException;

import jakarta.servlet.http.HttpServletRequest;

public class AuthorizationExtractor {

	private static final String BEARER_TYPE = "Bearer ";

	public static String extract(final HttpServletRequest request) {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader == null) {
			throw new EmptyAuthorizationHeaderException();
		}

		validateAuthorizationHeaderFormat(authorizationHeader);
		return authorizationHeader.substring(BEARER_TYPE.length()).trim();
	}

	private static void validateAuthorizationHeaderFormat(final String authorizationHeader) {
		if (!authorizationHeader.toLowerCase().startsWith(BEARER_TYPE.toLowerCase())) {
			throw new InvalidTokenException("부적절한 token 형식입니다.");
		}
	}
}
