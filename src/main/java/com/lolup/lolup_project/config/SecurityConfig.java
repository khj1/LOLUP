package com.lolup.lolup_project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.lolup.lolup_project.auth.application.JwtTokenProvider;
import com.lolup.lolup_project.auth.presentation.JwtAuthenticationFilter;
import com.lolup.lolup_project.config.oauth.CustomAuthenticationEntryPoint;
import com.lolup.lolup_project.config.oauth.CustomOAuth2SuccessHandler;
import com.lolup.lolup_project.config.oauth.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private static final String[] PATTERNS = {
			"/", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/h2-console/**",
			"/auth/refresh", "/auth/logout"
	};

	private final JwtTokenProvider jwtTokenProvider;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	@Bean
	protected SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable);
		http.formLogin(AbstractHttpConfigurer::disable);
		http.httpBasic(AbstractHttpConfigurer::disable);

		http.sessionManagement(sessionManagement -> sessionManagement
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.authorizeHttpRequests(request -> request
				.requestMatchers(PATTERNS).permitAll()
				.anyRequest().authenticated()
		);

		http.oauth2Login(oauth2 -> oauth2
				.userInfoEndpoint(userInfo -> userInfo
						.userService(customOAuth2UserService)
				)
				.successHandler(customOAuth2SuccessHandler)
		);

		http.exceptionHandling(exception -> exception
				.authenticationEntryPoint(customAuthenticationEntryPoint)
		);

		http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.addAllowedOrigin("/**");
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
