package com.lolup.lolup_project.member.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lolup.lolup_project.common.RepositoryTest;

class MemberRepositoryTest extends RepositoryTest {

	private static final String EMAIL = "aaa@bbb.ccc";

	@DisplayName("이메일로 멤버를 조회할 수 있다.")
	@Test
	void findByEmail() {
		Long memberId = memberRepository.save(createMember())
				.getId();

		Member findMember = memberRepository.findByEmail(EMAIL)
				.orElseThrow();

		assertThat(findMember.getId()).isEqualTo(memberId);
	}

	private Member createMember() {
		return Member.builder()
				.email(EMAIL)
				.build();
	}
}
