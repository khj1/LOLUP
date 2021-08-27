package com.lolup.lolup_project.config.oauth;

import com.lolup.lolup_project.member.Member;
import com.lolup.lolup_project.member.MemberRepository;
import com.lolup.lolup_project.member.Role;
import com.lolup.lolup_project.member.UserProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        log.info("Jwt 필터 호출됨");
        String token = jwtProvider.resolveToken((HttpServletRequest) request);

        log.info("Jwt 필터에서 호출된 토큰 값={}", token);

        if (token != null && jwtProvider.verifyToken(token)) {
            // 토큰이 유효하면 토큰으로 부터 유저 정보를 받아온다.
            String email = jwtProvider.getTokenClaims(token);
            Member member = memberRepository.findByEmail(email).get();
            UserProfile userProfile = Member.toUserProfileWithMember(member);

            Authentication auth = getAuthentication(userProfile);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(request, response);
    }

    private Authentication getAuthentication(UserProfile userProfile) {
        return new UsernamePasswordAuthenticationToken(userProfile, "",
                Arrays.asList(new SimpleGrantedAuthority(Role.USER.getKey())));
    }
}
