package com.lolup.lolup_project.duo.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.lolup.lolup_project.config.QuerydslConfig;
import com.lolup.lolup_project.duo.application.dto.DuoDto;
import com.lolup.lolup_project.duo.application.dto.SummonerDto;
import com.lolup.lolup_project.member.domain.Member;
import com.lolup.lolup_project.member.domain.MemberRepository;
import com.lolup.lolup_project.riot.summoner.domain.MostInfo;

import jakarta.persistence.EntityManager;

@Import(QuerydslConfig.class)
@DataJpaTest
class DuoRepositoryTest {

	@Autowired
	private EntityManager em;

	@Autowired
	private DuoRepository duoRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Test
	public void 듀오_추가_테스트() {
		//given
		Duo duo = getDuo(SummonerTier.BRONZE, SummonerPosition.BOT);

		//when
		duoRepository.save(duo);

		//then
		Duo findDuo = duoRepository.findById(duo.getId()).orElse(null);
		assertThat(duo.getDesc()).isEqualTo(findDuo.getDesc());
		assertThat(duo.getId()).isEqualTo(findDuo.getId());
	}

	@Test
	void 듀오_필터적용_조회() {
		// given
		duoRepository.save(getDuo(SummonerTier.GOLD, SummonerPosition.JUG));
		duoRepository.save(getDuo(SummonerTier.GOLD, SummonerPosition.TOP));
		duoRepository.save(getDuo(SummonerTier.SILVER, SummonerPosition.JUG));
		duoRepository.save(getDuo(SummonerTier.SILVER, SummonerPosition.TOP));

		// when
		PageRequest page = PageRequest.of(0, 20);

		Page<DuoDto> gold_jug = duoRepository.findAll(SummonerPosition.JUG, SummonerTier.GOLD, page);
		Page<DuoDto> gold = duoRepository.findAll(null, SummonerTier.GOLD, page);
		Page<DuoDto> all = duoRepository.findAll(null, null, page);

		List<DuoDto> list_gold_jug = gold_jug.getContent();
		List<DuoDto> list_gold = gold.getContent();
		List<DuoDto> list_all = all.getContent();

		long size_gold_jug = gold_jug.getNumberOfElements();
		long count_gold_jug = list_gold_jug.stream()
				.filter(dto ->
						dto.getPosition().equals(SummonerPosition.JUG) &&
								dto.getTier().equals(SummonerTier.GOLD)
				)
				.count();

		long size_gold = gold.getNumberOfElements();
		long count_gold = list_gold.stream()
				.filter(dto -> dto.getTier().equals(SummonerTier.GOLD))
				.count();

		long size_all = all.getTotalElements();

		//then
		assertThat(size_gold_jug).isEqualTo(count_gold_jug).isLessThan(size_all);
		assertThat(size_gold).isEqualTo(count_gold).isLessThan(size_all);
	}

	@Test
	void 데이터_수정() {
		//given
		Long beforeId = duoRepository.save(getDuo(SummonerTier.UNRANKED, SummonerPosition.MID)).getId();

		em.flush();
		em.clear();

		//when
		duoRepository.findById(beforeId).orElse(null).update(SummonerPosition.BOT, "updated");

		em.flush();
		em.clear();

		Duo findDuo = duoRepository.findById(beforeId).orElse(null);

		//then
		assertThat(findDuo.getDesc()).isEqualTo("updated");
		assertThat(findDuo.getPosition()).isEqualTo(SummonerPosition.BOT);
	}

	@Test
	public void 데이터_삭제() throws Exception {
		//given
		Duo duo = getDuo(SummonerTier.UNRANKED, SummonerPosition.SUP);
		duoRepository.save(duo);

		Long duoId = duo.getId();
		Long memberId = duo.getMember().getId();

		//when
		em.flush();
		em.clear();

		duoRepository.deleteByIdAndMemberId(duoId, memberId);

		em.flush();
		em.clear();

		//then
		assertThat(duoRepository.findById(duoId).orElse(null)).isNull();
	}

	private Duo getDuo(final SummonerTier tier, final SummonerPosition position) {
		Member member = Member.builder()
				.name(position.name() + " " + tier.name())
				.build();

		memberRepository.save(member);

		List<MostInfo> most3 = new ArrayList<>();
		most3.add(MostInfo.create("Syndra", 4L));
		most3.add(MostInfo.create("Lucian", 3L));
		most3.add(MostInfo.create("Zed", 2L));

		SummonerRankInfo info = SummonerRankInfo.builder()
				.summonerName("summonerName")
				.rank(SummonerRank.III)
				.tier(tier)
				.wins(100)
				.losses(100)
				.build();

		SummonerDto summonerDto = new SummonerDto(100, 0.2d, info, most3);

		return Duo.create(member, summonerDto, position, "hi");
	}
}
