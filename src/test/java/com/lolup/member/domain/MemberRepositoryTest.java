package com.lolup.member.domain;

import static com.lolup.common.fixture.MemberFixture.테스트_회원;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lolup.common.RepositoryTest;

class MemberRepositoryTest extends RepositoryTest {

	@DisplayName("이메일로 멤버를 조회할 수 있다.")
	@Test
	void findByEmail() {
		Member member = memberRepository.save(테스트_회원());

		Member findMember = memberRepository.findByEmail(member.getEmail())
				.orElseThrow();

		assertThat(findMember.getId()).isEqualTo(member.getId());
	}
}
