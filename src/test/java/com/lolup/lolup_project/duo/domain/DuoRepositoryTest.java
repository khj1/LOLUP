package com.lolup.lolup_project.duo.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.lolup.lolup_project.common.RepositoryTest;
import com.lolup.lolup_project.duo.application.dto.DuoDto;
import com.lolup.lolup_project.member.domain.Member;
import com.lolup.lolup_project.riot.summoner.domain.ChampionStat;

class DuoRepositoryTest extends RepositoryTest {

	private static final String DESC = "testDesc";
	private static final double LATEST_WIN_RATE = 0.2d;

	@DisplayName("필터를 통해 듀오를 조회한다.")
	@Test
	void findAll() {
		duoRepository.save(createDuo(SummonerTier.GOLD, SummonerPosition.JUG));
		duoRepository.save(createDuo(SummonerTier.GOLD, SummonerPosition.TOP));
		duoRepository.save(createDuo(SummonerTier.SILVER, SummonerPosition.JUG));
		duoRepository.save(createDuo(SummonerTier.SILVER, SummonerPosition.TOP));

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
		Duo duo = createDuo(SummonerTier.UNRANKED, SummonerPosition.MID);
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
		Duo savedDuo = duoRepository.save(createDuo(SummonerTier.UNRANKED, SummonerPosition.SUP));

		Long duoId = savedDuo.getId();
		Long memberId = savedDuo.getMember().getId();

		Duo findDuo = duoRepository.findByIdAndMemberId(duoId, memberId)
				.orElseThrow();

		assertThat(findDuo)
				.usingRecursiveComparison()
				.isEqualTo(savedDuo);
	}

	private Duo createDuo(final SummonerTier tier, final SummonerPosition position) {
		Member member = createMember(tier, position);
		memberRepository.save(member);

		List<ChampionStat> championStats = createChampionStats();
		SummonerStat summonerStat = createSummonerStat(tier);

		return Duo.create(member, summonerStat, championStats, LATEST_WIN_RATE, position, DESC);
	}

	private Member createMember(final SummonerTier tier, final SummonerPosition position) {
		return Member.builder()
				.name(position.name() + " " + tier.name())
				.build();
	}

	private List<ChampionStat> createChampionStats() {
		List<ChampionStat> championStats = new ArrayList<>();
		championStats.add(ChampionStat.create("Syndra", 4L));
		championStats.add(ChampionStat.create("Lucian", 3L));
		championStats.add(ChampionStat.create("Zed", 2L));

		return championStats;
	}

	private SummonerStat createSummonerStat(final SummonerTier tier) {
		return SummonerStat.builder()
				.summonerName("summonerName")
				.rank(SummonerRank.III)
				.tier(tier)
				.wins(100)
				.losses(100)
				.build();
	}
}
