package com.lolup.member.domain;

import static com.lolup.common.fixture.MemberFixture.소환사_등록_회원;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lolup.common.RepositoryTest;

class MemberRepositoryTest extends RepositoryTest {

	@DisplayName("이메일과 소셜 타입으로 멤버를 조회할 수 있다.")
	@Test
	void findByEmailAndSocialType() {
		Member member = memberRepository.save(소환사_등록_회원());

		Member findMember = memberRepository.findByEmailAndSocialType(member.getEmail(), member.getSocialType())
				.orElseThrow();

		assertThat(findMember.getId()).isEqualTo(member.getId());
	}
}
