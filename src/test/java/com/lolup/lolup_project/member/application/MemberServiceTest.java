package com.lolup.lolup_project.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lolup.lolup_project.common.ServiceTest;
import com.lolup.lolup_project.member.domain.Member;
import com.lolup.lolup_project.member.exception.NoSuchMemberException;

class MemberServiceTest extends ServiceTest {

	private static final String SUMMONER_NAME = "testSummonerName";
	private static final String UPDATED_SUMMONER_NAME = "updatedSummonerName";
	private static final long INVALID_MEMBER_ID = 99L;

	@DisplayName("소환사 이름을 변경한다.")
	@Test
	void updateSummonerName() {
		Member member = memberRepository.save(createMember());
		Long memberId = member.getId();

		memberService.updateSummonerName(memberId, UPDATED_SUMMONER_NAME);
		Member findMember = memberRepository.findById(memberId)
				.orElseThrow();

		assertThat(findMember.getSummonerName()).isEqualTo(UPDATED_SUMMONER_NAME);
	}

	@DisplayName("소환사 이름을 변경 시 멤버 ID가 유효하지 않으면 예외가 발생한다.")
	@Test
	void updateSummonerNameWithInvalidMemberId() {
		memberRepository.save(createMember());

		assertThatThrownBy(() -> memberService.updateSummonerName(INVALID_MEMBER_ID, UPDATED_SUMMONER_NAME))
				.isInstanceOf(NoSuchMemberException.class);
	}

	private Member createMember() {
		return Member.builder()
				.summonerName(SUMMONER_NAME)
				.build();
	}
}
