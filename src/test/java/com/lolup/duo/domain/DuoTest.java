package com.lolup.duo.domain;

import static com.lolup.common.fixture.DuoFixture.테스트_듀오;
import static com.lolup.common.fixture.MemberFixture.소환사_등록_회원;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DuoTest {

	public static final String UPDATED_DESC = "updated description";

	@DisplayName("듀오 모집글의 포지션과 설명을 변경할 수 있다.")
	@Test
	void update() {
		Duo duo = 테스트_듀오(소환사_등록_회원(), SummonerPosition.JUG, SummonerTier.GOLD);

		duo.update(SummonerPosition.BOT, UPDATED_DESC);

		assertThat(duo)
				.extracting("position", "desc")
				.containsExactly(SummonerPosition.BOT, UPDATED_DESC);
	}
}
