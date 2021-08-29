package com.lolup.lolup_project.config.oauth;

import com.lolup.lolup_project.member.MemberRepository;
import com.lolup.lolup_project.member.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터 체인에 등록된다.
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final OAuthService oAuthService;
    private final OAuth2SuccessHandler successHandler;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.addFilterAfter(jwtAuthenticationFilter, LogoutFilter.class);

        http
                .cors()
                .and()
                // rest API이므로 csrf 비활성화
                    .csrf().disable()
                    .formLogin().disable()
                // rest api만을 고려하기 때문에 기본 설정은 해제한다.
                    .httpBasic().disable()
                // 세션 비활성화
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeRequests()
//                        .antMatchers("/").permitAll()
//                        .antMatchers("/token/**").permitAll()
//                        .antMatchers("/oauth/**").permitAll()
//                        .antMatchers("/auth/refresh").permitAll()
//                        .antMatchers("/auth/check").hasRole(Role.USER.getKey())
//                        .antMatchers("/duo/{duoId}").hasRole("USER")
                        .anyRequest().permitAll()
                .and()
                // 스프링 MVC의 예외 처리( 오류 페이지 반환 )를 비활성화 한다.
                    .exceptionHandling().disable()
                    .oauth2Login()
                        .successHandler(successHandler)
                        .userInfoEndpoint().userService(oAuthService);


        http.addFilterBefore(new JwtAuthFilter(jwtProvider, memberRepository), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
