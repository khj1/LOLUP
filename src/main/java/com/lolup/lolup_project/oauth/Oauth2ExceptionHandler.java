package com.lolup.lolup_project.oauth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.lolup.lolup_project.auth.exception.EmptyAuthorizationHeaderException;
import com.lolup.lolup_project.auth.exception.InvalidTokenException;
import com.lolup.lolup_project.auth.exception.NoAuthenticationException;
import com.lolup.lolup_project.auth.exception.NoSuchRefreshTokenException;
import com.lolup.lolup_project.common.ErrorResult;

@RestControllerAdvice
public class Oauth2ExceptionHandler {

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler({
			EmptyAuthorizationHeaderException.class,
			InvalidTokenException.class,
			NoSuchRefreshTokenException.class,
			NoAuthenticationException.class
	})
	public ErrorResult emptyAuthorizationHeaderException(Exception e) {
		return new ErrorResult("401", e.getMessage());
	}
}
