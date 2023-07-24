package com.lolup.lolup_project.duo.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.lolup.lolup_project.config.JpaAuditingConfig;
import com.lolup.lolup_project.config.QuerydslConfig;
import com.lolup.lolup_project.duo.application.dto.DuoDto;
import com.lolup.lolup_project.duo.application.dto.SummonerDto;
import com.lolup.lolup_project.member.domain.Member;
import com.lolup.lolup_project.member.domain.MemberRepository;
import com.lolup.lolup_project.riot.summoner.domain.MostInfo;

import jakarta.persistence.EntityManager;

@Import({QuerydslConfig.class, JpaAuditingConfig.class})
@DataJpaTest
class DuoRepositoryTest {

	@Autowired
	private EntityManager em;

	@Autowired
	private DuoRepository duoRepository;

	@Autowired
	private MemberRepository memberRepository;

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

	@DisplayName("듀오 ID와 멤버 ID로 듀오를 삭제한다.")
	@Test
	void delete() {
		Duo duo = createDuo(SummonerTier.UNRANKED, SummonerPosition.SUP);
		duoRepository.save(duo);

		Long duoId = duo.getId();
		Long memberId = duo.getMember().getId();

		duoRepository.deleteByIdAndMemberId(duoId, memberId);

		assertThat(duoRepository.findById(duoId)).isEmpty();
	}

	private Duo createDuo(final SummonerTier tier, final SummonerPosition position) {
		Member member = createMember(tier, position);
		memberRepository.save(member);

		List<MostInfo> most3 = new ArrayList<>();
		most3.add(MostInfo.create("Syndra", 4L));
		most3.add(MostInfo.create("Lucian", 3L));
		most3.add(MostInfo.create("Zed", 2L));

		SummonerRankInfo info = createSummonerRankInfo(tier);
		SummonerDto summonerDto = new SummonerDto(100, 0.2d, info, most3);

		return Duo.create(member, summonerDto, position, "hi");
	}

	private Member createMember(final SummonerTier tier, final SummonerPosition position) {
		return Member.builder()
				.name(position.name() + " " + tier.name())
				.build();
	}

	private SummonerRankInfo createSummonerRankInfo(final SummonerTier tier) {
		return SummonerRankInfo.builder()
				.summonerName("summonerName")
				.rank(SummonerRank.III)
				.tier(tier)
				.wins(100)
				.losses(100)
				.build();
	}
}
