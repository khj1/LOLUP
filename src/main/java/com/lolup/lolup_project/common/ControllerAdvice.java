package com.lolup.lolup_project.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.lolup.lolup_project.auth.exception.EmptyAuthorizationHeaderException;
import com.lolup.lolup_project.auth.exception.InvalidTokenException;
import com.lolup.lolup_project.auth.exception.NoAuthenticationException;
import com.lolup.lolup_project.auth.exception.NoSuchRefreshTokenException;
import com.lolup.lolup_project.duo.exception.DuoDeleteFailureException;
import com.lolup.lolup_project.duo.exception.NoSuchDuoException;
import com.lolup.lolup_project.member.exception.NoSuchMemberException;

@RestControllerAdvice
public class ControllerAdvice {

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler({
			EmptyAuthorizationHeaderException.class,
			InvalidTokenException.class,
			NoSuchRefreshTokenException.class,
			NoAuthenticationException.class
	})
	public ErrorResponse handleAuthorizationException(final Exception e) {
		return new ErrorResponse("401", e.getMessage());
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler({
			NoSuchDuoException.class,
			NoSuchMemberException.class,
			DuoDeleteFailureException.class
	})
	public ErrorResponse handleNotFoundException(final Exception e) {
		return new ErrorResponse("404", e.getMessage());
	}
}
