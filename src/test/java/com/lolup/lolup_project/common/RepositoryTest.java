package com.lolup.lolup_project.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.lolup.lolup_project.auth.domain.RefreshTokenRepository;
import com.lolup.lolup_project.config.JpaAuditingConfig;
import com.lolup.lolup_project.config.QuerydslConfig;
import com.lolup.lolup_project.duo.domain.DuoRepository;
import com.lolup.lolup_project.member.domain.MemberRepository;

import jakarta.persistence.EntityManager;

@Import({JpaAuditingConfig.class, QuerydslConfig.class})
@DataJpaTest
@ActiveProfiles("test")
public abstract class RepositoryTest {

	@Autowired
	protected EntityManager em;

	@Autowired
	protected MemberRepository memberRepository;

	@Autowired
	protected RefreshTokenRepository refreshTokenRepository;

	@Autowired
	protected DuoRepository duoRepository;
}
