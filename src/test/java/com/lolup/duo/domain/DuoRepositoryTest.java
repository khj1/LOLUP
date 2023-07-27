package com.lolup.duo.domain;

import static com.lolup.common.fixture.DuoFixture.테스트_듀오;
import static com.lolup.common.fixture.MemberFixture.소환사_등록_회원;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.lolup.common.RepositoryTest;
import com.lolup.duo.application.dto.DuoDto;
import com.lolup.member.domain.Member;

class DuoRepositoryTest extends RepositoryTest {

	@DisplayName("필터를 통해 듀오를 조회한다.")
	@Test
	void findAll() {
		Member member = memberRepository.save(소환사_등록_회원());
		duoRepository.save(테스트_듀오(member, SummonerPosition.JUG, SummonerTier.GOLD));
		duoRepository.save(테스트_듀오(member, SummonerPosition.TOP, SummonerTier.GOLD));
		duoRepository.save(테스트_듀오(member, SummonerPosition.JUG, SummonerTier.SILVER));
		duoRepository.save(테스트_듀오(member, SummonerPosition.TOP, SummonerTier.SILVER));

		PageRequest page = PageRequest.of(0, 20);

		Page<DuoDto> goldJug = duoRepository.findAll(SummonerPosition.JUG, SummonerTier.GOLD, page);
		Page<DuoDto> gold = duoRepository.findAll(null, SummonerTier.GOLD, page);
		Page<DuoDto> all = duoRepository.findAll(null, null, page);

		long sizeOfGoldJug = goldJug.getNumberOfElements();
		long sizeOfGold = gold.getNumberOfElements();
		long totalSize = all.getTotalElements();

		assertAll(
				() -> assertThat(sizeOfGoldJug).isEqualTo(1),
				() -> assertThat(sizeOfGold).isEqualTo(2),
				() -> assertThat(totalSize).isEqualTo(4)
		);
	}

	@DisplayName("듀오 수정 시 수정일자가 올바르게 나온다.")
	@Test
	void update() {
		Member member = memberRepository.save(소환사_등록_회원());
		Duo duo = 테스트_듀오(member, SummonerPosition.MID, SummonerTier.UNRANKED);
		Long memberId = duoRepository.save(duo).getId();

		duo.update(SummonerPosition.BOT, "updated");
		em.flush();

		Duo findDuo = duoRepository.findById(memberId)
				.orElseThrow();

		assertThat(findDuo.getLastModifiedDate()).isAfter(findDuo.getCreatedDate());
	}

	@DisplayName("듀오 ID와 멤버 ID로 듀오를 조회할 수 있다.")
	@Test
	void delete() {
		Member member = memberRepository.save(소환사_등록_회원());
		Duo savedDuo = duoRepository.save(테스트_듀오(member, SummonerPosition.SUP, SummonerTier.UNRANKED));

		Long duoId = savedDuo.getId();
		Long memberId = savedDuo.getMember().getId();

		Duo findDuo = duoRepository.findByIdAndMemberId(duoId, memberId)
				.orElseThrow();

		assertThat(findDuo)
				.usingRecursiveComparison()
				.isEqualTo(savedDuo);
	}
}
