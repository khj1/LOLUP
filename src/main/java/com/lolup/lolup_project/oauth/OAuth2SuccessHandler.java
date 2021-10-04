package com.lolup.lolup_project.oauth;

import com.lolup.lolup_project.member.Member;
import com.lolup.lolup_project.member.MemberRepository;
import com.lolup.lolup_project.member.UserProfile;
import com.lolup.lolup_project.token.JwtProvider;
import com.lolup.lolup_project.token.RefreshToken;
import com.lolup.lolup_project.token.RefreshTokenRepository;
import com.lolup.lolup_project.token.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${front.redirect_url}")
    private String redirect_url;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 success handler 호출됨");

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = authToken.getAuthorizedClientRegistrationId();

        UserProfile userProfile = OAuthAttributes.extract(registrationId, attributes);

        Member member = memberRepository.findByEmail(userProfile.getEmail()).get();
        Token token = jwtProvider.generateToken(userProfile.getEmail(), "USER");

        log.info("tokens={}", token);

        RefreshToken refreshToken = RefreshToken.create(member, token.getRefreshToken());
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

        log.info("get refresh token after refresh token saved = {}", savedRefreshToken.getRefreshToken());

        writeTokenResponse(response, token);
    }

    private void writeTokenResponse(HttpServletResponse response, Token token) throws IOException{

        Cookie cookie = new Cookie("refreshToken", token.getRefreshToken());
        cookie.setMaxAge(60 * 60 * 24 * 30); // 1달
        cookie.setSecure(false);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        response.addCookie(cookie);

        log.info("refresh token cookie generated={} : {}", cookie.getName(), cookie.getValue());

        response.sendRedirect(redirect_url + token.getToken());
    }
}
