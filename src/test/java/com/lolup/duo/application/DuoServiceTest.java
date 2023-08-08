package com.lolup.duo.application;

import static com.lolup.common.fixture.DuoFixture.듀오_생성_요청;
import static com.lolup.common.fixture.DuoFixture.테스트_듀오;
import static com.lolup.common.fixture.DuoFixture.테스트_소환사_계정;
import static com.lolup.common.fixture.DuoFixture.테스트_소환사_전적;
import static com.lolup.common.fixture.DuoFixture.테스트_최근_전적;
import static com.lolup.common.fixture.MemberFixture.소환사_등록_회원;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import com.lolup.common.ServiceTest;
import com.lolup.duo.application.dto.DuoResponse;
import com.lolup.duo.domain.Duo;
import com.lolup.duo.domain.SummonerPosition;
import com.lolup.duo.domain.SummonerTier;
import com.lolup.duo.exception.DuoCreationLimitException;
import com.lolup.duo.exception.DuoDeleteFailureException;
import com.lolup.duo.exception.DuoUpdateFailureException;
import com.lolup.duo.exception.NoSuchDuoException;
import com.lolup.member.domain.Member;
import com.lolup.member.exception.NoSuchMemberException;

class DuoServiceTest extends ServiceTest {

	public static final int CREATION_LIMIT = 10;
	private static final String GAME_VERSION = "test.game.version";
	private static final long INVALID_MEMBER_ID = 99L;
	private static final long INVALID_DUO_ID = 99L;
	private static final LocalDateTime NOW = LocalDateTime.now();

	@DisplayName("조건에 맞는 듀오 모집글을 조회한다.")
	@Test
	void findAll() {
		given(riotStaticService.getLatestGameVersion()).willReturn(GAME_VERSION);

		Member member = memberRepository.save(소환사_등록_회원());
		duoRepository.save(테스트_듀오(member, SummonerPosition.JUG, SummonerTier.GOLD));
		duoRepository.save(테스트_듀오(member, SummonerPosition.TOP, SummonerTier.GOLD));
		duoRepository.save(테스트_듀오(member, SummonerPosition.JUG, SummonerTier.SILVER));
		duoRepository.save(테스트_듀오(member, SummonerPosition.TOP, SummonerTier.SILVER));

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
				.willReturn(테스트_소환사_계정());
		given(summonerService.requestSummonerStat(anyString(), anyString()))
				.willReturn(테스트_소환사_전적(SummonerTier.CHALLENGER));
		given(matchService.requestRecentMatchStats(anyString()))
				.willReturn(테스트_최근_전적());

		Long memberId = memberRepository.save(소환사_등록_회원())
				.getId();

		duoService.save(memberId, 듀오_생성_요청(), NOW);

		assertThat(duoRepository.findAll()).hasSize(1);
	}

	@DisplayName("듀오 모집글 추가 시 잘못된 멤버 ID를 입력하면 예외가 발생한다.")
	@Test
	void saveWithInvalidMemberId() {
		assertThatThrownBy(() -> duoService.save(INVALID_MEMBER_ID, 듀오_생성_요청(), NOW))
				.isInstanceOf(NoSuchMemberException.class);
	}

	@DisplayName("생성 제한 시간 이내에 듀오 모집글을 추가하면 예외가 발생한다.")
	@Test
	void saveWithBeforeCreationLimit() {
		Member member = memberRepository.save(소환사_등록_회원());
		duoRepository.save(테스트_듀오(member, SummonerPosition.MID, SummonerTier.PLATINUM));
		LocalDateTime beforeCreationLimit = NOW.minusMinutes(CREATION_LIMIT).plusSeconds(1L);

		assertThatThrownBy(() -> duoService.save(member.getId(), 듀오_생성_요청(), beforeCreationLimit))
				.isInstanceOf(DuoCreationLimitException.class);
	}

	@DisplayName("듀오 모집글을 수정한다.")
	@Test
	void update() {
		Member member = memberRepository.save(소환사_등록_회원());
		Duo duo = duoRepository.save(테스트_듀오(member, SummonerPosition.MID, SummonerTier.PLATINUM));
		Long duoId = duo.getId();

		duoService.update(member.getId(), duoId, SummonerPosition.SUP, duo.getDesc());

		Duo findDuo = duoRepository.findById(duoId)
				.orElseThrow();

		assertThat(findDuo.getPosition()).isEqualTo(SummonerPosition.SUP);
	}

	@DisplayName("듀오 모집글 수정 시 듀오 ID가 유효하지 않으면 예외가 발생한다.")
	@Test
	void updateWithInvalidDuoId() {
		Member member = memberRepository.save(소환사_등록_회원());
		Duo duo = duoRepository.save(테스트_듀오(member, SummonerPosition.MID, SummonerTier.PLATINUM));

		assertThatThrownBy(() -> duoService.update(member.getId(), INVALID_DUO_ID, SummonerPosition.SUP, duo.getDesc()))
				.isInstanceOf(NoSuchDuoException.class);
	}

	@DisplayName("듀오 모집글 수정 시 멤버 ID가 유효하지 않으면 예외가 발생한다.")
	@Test
	void updateWithInvalidMember() {
		Member member = memberRepository.save(소환사_등록_회원());
		Duo duo = duoRepository.save(테스트_듀오(member, SummonerPosition.MID, SummonerTier.PLATINUM));

		assertThatThrownBy(() -> duoService.update(INVALID_MEMBER_ID, duo.getId(), SummonerPosition.SUP, duo.getDesc()))
				.isInstanceOf(DuoUpdateFailureException.class);
	}

	@DisplayName("듀오 모집글을 제거한다.")
	@Test
	void delete() {
		Member member = memberRepository.save(소환사_등록_회원());
		Duo duo = duoRepository.save(테스트_듀오(member, SummonerPosition.JUG, SummonerTier.GOLD));

		Long memberId = member.getId();
		Long duoId = duo.getId();

		duoService.delete(duoId, memberId);

		assertThat(duoRepository.findById(duoId)).isEmpty();
	}

	@DisplayName("유효하지 않은 회원 ID로 듀오 모집글을 제거하려고 하면 예외가 발생한다..")
	@Test
	void deleteWithInvalidMemberId() {
		Member member = memberRepository.save(소환사_등록_회원());
		Duo duo = duoRepository.save(테스트_듀오(member, SummonerPosition.JUG, SummonerTier.GOLD));

		Long duoId = duo.getId();

		assertThatThrownBy(() -> duoService.delete(duoId, INVALID_MEMBER_ID))
				.isInstanceOf(DuoDeleteFailureException.class);
	}

	@DisplayName("유효하지 않은 듀오 ID로 듀오 모집글을 제거하려고 하면 예외가 발생한다..")
	@Test
	void deleteWithInvalidDuoId() {
		Member member = memberRepository.save(소환사_등록_회원());
		duoRepository.save(테스트_듀오(member, SummonerPosition.JUG, SummonerTier.GOLD));

		Long memberId = member.getId();

		assertThatThrownBy(() -> duoService.delete(INVALID_DUO_ID, memberId))
				.isInstanceOf(DuoDeleteFailureException.class);
	}
}
