package com.lolup.auth.presentation;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.lolup.auth.exception.NoAuthenticationException;

@Component
public class AuthenticationPrincipalArgumentResolver implements HandlerMethodArgumentResolver {

	private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
			.getContextHolderStrategy();

	@Override
	public boolean supportsParameter(final MethodParameter parameter) {
		return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
	}

	@Override
	public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
								  final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) {
		Authentication authentication = securityContextHolderStrategy.getContext().getAuthentication();
		if (authentication == null) {
			throw new NoAuthenticationException();
		}
		return authentication.getPrincipal();
	}
}
