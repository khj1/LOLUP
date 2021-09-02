package com.lolup.lolup_project;

import com.lolup.lolup_project.config.oauth.JwtProvider;
import com.lolup.lolup_project.config.oauth.Token;
import com.lolup.lolup_project.member.Member;
import com.lolup.lolup_project.member.MemberRepository;
import com.lolup.lolup_project.member.Role;
import com.lolup.lolup_project.member.UserProfile;
import com.lolup.lolup_project.token.RefreshToken;
import com.lolup.lolup_project.token.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.CookieGenerator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public Map<String, Object> checkAuth(Principal principal) {
        UserProfile userProfile = (UserProfile) ((Authentication) principal).getPrincipal();
        Member member = memberRepository.findByEmail(userProfile.getEmail()).get();

        Map<String, Object> map = new HashMap<>();
        map.put("memberId", member.getMemberId());
        map.put("summonerName", member.getSummonerName());
        map.put("nickname", null);
        map.put("login", true);

        return map;
    }

    public Map<String, Object> refresh(String refreshToken, HttpServletResponse response) {

        if (!jwtProvider.verifyToken(refreshToken)) {
            throw new IllegalArgumentException("리프레시 토큰이 만료되었습니다.");
        }

        String email = jwtProvider.getTokenClaims(refreshToken);
        Token newToken = jwtProvider.generateToken(email, Role.USER.getKey());
        Member member = memberRepository.findByEmail(email).get();

        RefreshToken newRefreshToken = RefreshToken.create(member.getMemberId(), refreshToken);
        String savedRefreshToken = refreshTokenRepository.save(newRefreshToken);

        setCookie(response, savedRefreshToken);

        Map<String, Object> map = new HashMap<>();
        map.put("token", newToken.getToken());

        return map;
    }

    public Map<String, Object> logout(Principal principal, HttpServletResponse response) {
        UserProfile userProfile = (UserProfile) ((Authentication) principal).getPrincipal();
        Member member = memberRepository.findByEmail(userProfile.getEmail()).get();

        refreshTokenRepository.delete(member.getMemberId());
        deleteCookie(response);

        Map<String, Object> map = new HashMap<>();
        map.put("logout", true);

        return map;
    }

    private void setCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setMaxAge(60 * 60 * 24 * 30); // 1달
        cookie.setSecure(false);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private void deleteCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setSecure(false);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
