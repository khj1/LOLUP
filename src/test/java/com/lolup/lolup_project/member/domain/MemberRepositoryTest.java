package com.lolup.lolup_project.member.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.lolup.lolup_project.config.QuerydslConfig;

@Import(QuerydslConfig.class)
@DataJpaTest
class MemberRepositoryTest {

	@Autowired
	MemberRepository memberRepository;

	@Test
	public void 중복은_업데이트() {
		//given
		String email = "my@email.com";

		Member member1 = Member.builder()
				.name("김떙땡")
				.picture(null)
				.role(Role.USER)
				.email("my@email.com")
				.summonerName(null)
				.build();

		//when
		memberRepository.save(member1);

		Member member2 = Member.builder()
				.id(member1.getId())
				.name("김띵띵")
				.picture(null)
				.role(Role.USER)
				.email("my@email.com")
				.summonerName(null)
				.build();

		memberRepository.save(member2);

		//then
		String resultName = memberRepository.findByEmail(email).orElse(null).getName();

		assertThat(resultName).isEqualTo("김띵띵");

	}
}
