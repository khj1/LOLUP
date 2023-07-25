package com.lolup.lolup_project.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.lolup.lolup_project.auth.application.AuthService;
import com.lolup.lolup_project.auth.application.JwtTokenProvider;
import com.lolup.lolup_project.auth.domain.RefreshTokenRepository;
import com.lolup.lolup_project.duo.application.DuoService;
import com.lolup.lolup_project.duo.domain.DuoRepository;
import com.lolup.lolup_project.member.application.MemberService;
import com.lolup.lolup_project.member.domain.MemberRepository;
import com.lolup.lolup_project.riot.match.application.MatchService;
import com.lolup.lolup_project.riot.riotstatic.RiotStaticService;
import com.lolup.lolup_project.riot.summoner.application.SummonerService;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public abstract class ServiceTest {

	@Autowired
	protected AuthService authService;

	@Autowired
	protected JwtTokenProvider jwtTokenProvider;

	@Autowired
	protected MemberService memberService;

	@Autowired
	protected MemberRepository memberRepository;

	@Autowired
	protected RefreshTokenRepository refreshTokenRepository;

	@Autowired
	protected DuoService duoService;

	@Autowired
	protected DuoRepository duoRepository;

	@MockBean
	protected MatchService matchService;

	@MockBean
	protected SummonerService summonerService;

	@MockBean
	protected RiotStaticService riotStaticService;
}
