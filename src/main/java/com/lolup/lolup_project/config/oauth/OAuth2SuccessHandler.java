package com.lolup.lolup_project.config.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolup.lolup_project.member.MemberRepository;
import com.lolup.lolup_project.member.UserProfile;
import com.lolup.lolup_project.token.RefreshToken;
import com.lolup.lolup_project.token.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Provider;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 success handler 호출됨");

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = authToken.getAuthorizedClientRegistrationId();

        UserProfile userProfile = OAuthAttributes.extract(registrationId, attributes);

        Long memberId = memberRepository.findByEmail(userProfile.getEmail()).get().getMemberId();
        Token token = jwtProvider.generateToken(userProfile.getEmail(), "USER");

        log.info("tokens={}", token);

        RefreshToken refreshToken = RefreshToken.create(memberId, token.getRefreshToken());
        String email = refreshTokenRepository.save(refreshToken);

        log.info("All email after refresh token saved = {}", email);

        writeTokenResponse(response, token);
    }

    private void writeTokenResponse(HttpServletResponse response, Token token) throws IOException{
        response.setContentType("text/html;charset=UTF-8");
        response.addHeader("X-AUTH-TOKEN", token.getToken());
        response.addHeader("X-REFRESH-TOKEN", token.getRefreshToken());
        response.setContentType("application/json;charset=UTF-8");

        log.info("JWT 토큰 헤더에 추가됨");

        PrintWriter writer = response.getWriter();
        writer.println(objectMapper.writeValueAsString(token));
        writer.flush();
    }

}
