package com.lolup.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.lolup.auth.application.JwtTokenProvider;
import com.lolup.auth.presentation.AuthenticationPrincipalArgumentResolver;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AuthenticationPrincipalConfig implements WebMvcConfigurer {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new AuthenticationPrincipalArgumentResolver(jwtTokenProvider));
	}
}
