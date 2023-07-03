package com.lolup.lolup_project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.lolup.lolup_project.member.MemberRepository;
import com.lolup.lolup_project.oauth.OAuth2SuccessHandler;
import com.lolup.lolup_project.oauth.OAuthService;
import com.lolup.lolup_project.token.JwtAuthFilter;
import com.lolup.lolup_project.token.JwtProvider;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터 체인에 등록된다.
@RequiredArgsConstructor
public class SecurityConfig {

	private final OAuthService oAuthService;
	private final OAuth2SuccessHandler successHandler;
	private final JwtProvider jwtProvider;
	private final MemberRepository memberRepository;

	@Value("${front.domain}")
	private String domain;

	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable()
				.formLogin().disable()
				.httpBasic().disable()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.exceptionHandling().disable()
				.oauth2Login()
				.successHandler(successHandler)
				.userInfoEndpoint().userService(oAuthService);

		http.addFilterBefore(new JwtAuthFilter(jwtProvider, memberRepository),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.addAllowedOrigin(domain);
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
