package com.lolup.lolup_project.token;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.lolup.lolup_project.auth.AuthorizationExtractor;
import com.lolup.lolup_project.auth.EmptyAuthorizationHeaderException;
import com.lolup.lolup_project.auth.InvalidTokenException;
import com.lolup.lolup_project.member.Member;
import com.lolup.lolup_project.member.MemberRepository;
import com.lolup.lolup_project.member.Role;
import com.lolup.lolup_project.member.UserProfile;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String[] AUTHORIZED_PATTERN = {"/duo*", "/member*", "/auth*"};

	private final JwtProvider jwtProvider;
	private final MemberRepository memberRepository;

	@Override
	protected boolean shouldNotFilter(final HttpServletRequest request) {
		return !PatternMatchUtils.simpleMatch(AUTHORIZED_PATTERN, request.getRequestURI());
	}

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
									final FilterChain filterChain) throws ServletException, IOException {
		try {
			String token = AuthorizationExtractor.extract(request);
			jwtProvider.verifyToken(token);

			UserProfile userProfile = extractUserProfileFrom(token);
			Authentication authentication = getAuthentication(userProfile);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (EmptyAuthorizationHeaderException | InvalidTokenException e) {
			request.setAttribute("exception", e);
		}
		filterChain.doFilter(request, response);
	}

	private UserProfile extractUserProfileFrom(final String token) {
		String email = jwtProvider.getTokenClaims(token);
		Member member = memberRepository.findByEmail(email)
				.orElseThrow(IllegalArgumentException::new);

		return UserProfile.create(member);
	}

	private Authentication getAuthentication(UserProfile userProfile) {
		return new UsernamePasswordAuthenticationToken(
				userProfile,
				null,
				List.of(new SimpleGrantedAuthority(Role.USER.getKey()))
		);
	}
}
