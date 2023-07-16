package com.lolup.lolup_project.oauth;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Qualifier("handlerExceptionResolver")
	private final HandlerExceptionResolver handlerExceptionResolver;

	@Override
	public void commence(final HttpServletRequest request, final HttpServletResponse response,
						 final AuthenticationException authException) {
		Exception exception = (Exception)request.getAttribute("exception");
		if (exception == null) {
			exception = authException;
		}
		handlerExceptionResolver.resolveException(request, response, null, exception);
	}
}
