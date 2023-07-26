package com.lolup.duo.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import com.lolup.common.ServiceTest;
import com.lolup.duo.application.dto.DuoResponse;
import com.lolup.duo.application.dto.DuoSaveRequest;
import com.lolup.duo.domain.Duo;
import com.lolup.duo.domain.SummonerPosition;
import com.lolup.duo.domain.SummonerRank;
import com.lolup.duo.domain.SummonerStat;
import com.lolup.duo.domain.SummonerTier;
import com.lolup.duo.exception.DuoDeleteFailureException;
import com.lolup.duo.exception.NoSuchDuoException;
import com.lolup.member.domain.Member;
import com.lolup.member.exception.NoSuchMemberException;
import com.lolup.riot.match.application.dto.RecentMatchStatsDto;
import com.lolup.riot.summoner.application.dto.SummonerAccountDto;
import com.lolup.riot.summoner.domain.ChampionStat;

class DuoServiceTest extends ServiceTest {

	private static final String GAME_VERSION = "test.game.version";
	private static final String DESC = "testDesc";
	private static final long INVALID_MEMBER_ID = 99L;
	private static final long INVALID_DUO_ID = 99L;
	private static final double LATEST_WIN_RATE = 0.2d;

	@DisplayName("조건에 맞는 듀오 모집글을 조회한다.")
	@Test
	void findAll() {
		given(riotStaticService.getLatestGameVersion()).willReturn(GAME_VERSION);

		Member member = memberRepository.save(createMember());
		duoRepository.save(createDuo(member, SummonerPosition.JUG, SummonerTier.GOLD));
		duoRepository.save(createDuo(member, SummonerPosition.TOP, SummonerTier.GOLD));
		duoRepository.save(createDuo(member, SummonerPosition.JUG, SummonerTier.SILVER));
		duoRepository.save(createDuo(member, SummonerPosition.TOP, SummonerTier.SILVER));

		PageRequest page = PageRequest.of(0, 20);

		DuoResponse 모든_게시물_조회_응답 = duoService.findAll(null, null, page);
		DuoResponse 정글_게시물_조회_응답 = duoService.findAll(SummonerPosition.JUG, null, page);
		DuoResponse 실버_탑_게시물_조회_응답 = duoService.findAll(SummonerPosition.TOP, SummonerTier.SILVER, page);

		assertAll(
				() -> assertThat(모든_게시물_조회_응답)
						.extracting("version", "totalCount")
						.containsExactly(GAME_VERSION, 4L),
				() -> assertThat(정글_게시물_조회_응답)
						.extracting("version", "totalCount")
						.containsExactly(GAME_VERSION, 2L),
				() -> assertThat(실버_탑_게시물_조회_응답)
						.extracting("version", "totalCount")
						.containsExactly(GAME_VERSION, 1L)
		);
	}

	@DisplayName("듀오 모집글을 생성한다.")
	@Test
	void save() {
		given(summonerService.requestAccountInfo(anyString()))
				.willReturn(createSummonerAccountDto());
		given(summonerService.requestSummonerStat(anyString(), anyString()))
				.willReturn(createSummonerStat(SummonerTier.CHALLENGER));
		given(matchService.requestRecentMatchStats(anyString(), anyString()))
				.willReturn(createRecentMatchStatsDto());

		Long memberId = memberRepository.save(createMember())
				.getId();

		duoService.save(memberId, createDuoSaveRequest());

		assertThat(duoRepository.findAll()).hasSize(1);
	}

	@DisplayName("듀오 모집글 추가 시 잘못된 멤버 ID를 입력하면 예외가 발생한다.")
	@Test
	void saveWithInvalidMemberId() {
		assertThatThrownBy(() -> duoService.save(INVALID_MEMBER_ID, createDuoSaveRequest()))
				.isInstanceOf(NoSuchMemberException.class);
	}

	@DisplayName("듀오 모집글을 수정한다.")
	@Test
	void update() {
		Member member = memberRepository.save(createMember());
		Duo duo = duoRepository.save(createDuo(member, SummonerPosition.MID, SummonerTier.PLATINUM));
		Long duoId = duo.getId();

		duoService.update(duoId, SummonerPosition.SUP, duo.getDesc());

		Duo findDuo = duoRepository.findById(duoId)
				.orElseThrow();

		assertThat(findDuo.getPosition()).isEqualTo(SummonerPosition.SUP);
	}

	@DisplayName("듀오 모집글 수정 시 듀오 ID가 유효하지 않으면 예외가 발생한다.")
	@Test
	void updateWithInvalidDuoId() {
		Member member = memberRepository.save(createMember());
		Duo duo = duoRepository.save(createDuo(member, SummonerPosition.MID, SummonerTier.PLATINUM));

		assertThatThrownBy(() -> duoService.update(INVALID_DUO_ID, SummonerPosition.SUP, duo.getDesc()))
				.isInstanceOf(NoSuchDuoException.class);
	}

	@DisplayName("듀오 모집글을 제거한다.")
	@Test
	void delete() {
		Member member = memberRepository.save(createMember());
		Duo duo = duoRepository.save(createDuo(member, SummonerPosition.JUG, SummonerTier.GOLD));

		Long memberId = member.getId();
		Long duoId = duo.getId();

		duoService.delete(duoId, memberId);

		assertThat(duoRepository.findById(duoId)).isEmpty();
	}

	@DisplayName("유효하지 않은 회원 ID로 듀오 모집글을 제거하려고 하면 예외가 발생한다..")
	@Test
	void deleteWithInvalidMemberId() {
		Member member = memberRepository.save(createMember());
		Duo duo = duoRepository.save(createDuo(member, SummonerPosition.JUG, SummonerTier.GOLD));

		Long duoId = duo.getId();

		assertThatThrownBy(() -> duoService.delete(duoId, INVALID_MEMBER_ID))
				.isInstanceOf(DuoDeleteFailureException.class);
	}

	@DisplayName("유효하지 않은 듀오 ID로 듀오 모집글을 제거하려고 하면 예외가 발생한다..")
	@Test
	void deleteWithInvalidDuoId() {
		Member member = memberRepository.save(createMember());
		duoRepository.save(createDuo(member, SummonerPosition.JUG, SummonerTier.GOLD));

		Long memberId = member.getId();

		assertThatThrownBy(() -> duoService.delete(INVALID_DUO_ID, memberId))
				.isInstanceOf(DuoDeleteFailureException.class);
	}

	private Duo createDuo(final Member member, final SummonerPosition position, final SummonerTier tier) {
		SummonerStat summonerStat = createSummonerStat(tier);
		List<ChampionStat> championStats = createChampionStats();

		return Duo.create(member, summonerStat, championStats, LATEST_WIN_RATE, position, DESC);
	}

	private Member createMember() {
		return Member.builder().build();
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

	private List<ChampionStat> createChampionStats() {
		List<ChampionStat> championStats = new ArrayList<>();
		championStats.add(ChampionStat.create("Syndra", 4L));
		championStats.add(ChampionStat.create("Lucian", 3L));
		championStats.add(ChampionStat.create("Zed", 2L));

		return championStats;
	}

	private SummonerAccountDto createSummonerAccountDto() {
		return SummonerAccountDto.builder()
				.id("testId")
				.name("testName")
				.puuid("testPuuid")
				.accountId("testAccountId")
				.profileIconId(1)
				.build();
	}

	private RecentMatchStatsDto createRecentMatchStatsDto() {
		return new RecentMatchStatsDto(1d, createChampionStats());
	}

	private DuoSaveRequest createDuoSaveRequest() {
		return DuoSaveRequest.builder()
				.summonerName("testSummonerName")
				.position(SummonerPosition.JUG)
				.desc("testDesc")
				.build();
	}
}
