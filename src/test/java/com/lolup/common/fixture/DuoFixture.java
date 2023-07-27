package com.lolup.common.fixture;

import static com.lolup.common.fixture.MemberFixture.테스트_회원;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.lolup.duo.application.dto.DuoDto;
import com.lolup.duo.application.dto.DuoResponse;
import com.lolup.duo.application.dto.DuoSaveRequest;
import com.lolup.duo.domain.Duo;
import com.lolup.duo.domain.SummonerPosition;
import com.lolup.duo.domain.SummonerRank;
import com.lolup.duo.domain.SummonerStat;
import com.lolup.duo.domain.SummonerTier;
import com.lolup.duo.presentation.dto.DuoUpdateRequest;
import com.lolup.member.domain.Member;
import com.lolup.riot.match.application.dto.RecentMatchStatsDto;
import com.lolup.riot.summoner.application.dto.SummonerAccountDto;
import com.lolup.riot.summoner.domain.ChampionStat;

public class DuoFixture {

	private static final double LATEST_WIN_RATE = 0.5d;
	private static final String DESC = "description";

	public static Duo 테스트_듀오(final Member member, final SummonerPosition position, final SummonerTier tier) {
		return new Duo(member, 테스트_소환사_전적(tier), 테스트_챔피언_사용횟수(), position, LATEST_WIN_RATE, DESC);
	}

	public static SummonerAccountDto 테스트_소환사_계정() {
		return new SummonerAccountDto("testAccountId", 1, 1L, "testName", "testId", "testPuuid", 1L);
	}

	public static SummonerStat 테스트_소환사_전적(final SummonerTier tier) {
		return new SummonerStat("summonerName", tier, SummonerRank.I, 20, 80);
	}

	public static List<ChampionStat> 테스트_챔피언_사용횟수() {
		List<ChampionStat> championStats = new ArrayList<>();
		championStats.add(탈리야());
		championStats.add(루시안());
		championStats.add(제드());

		return championStats;
	}

	private static ChampionStat 탈리야() {
		return ChampionStat.create("taliyah", 4L);
	}

	private static ChampionStat 루시안() {
		return ChampionStat.create("Lucian", 3L);
	}

	private static ChampionStat 제드() {
		return ChampionStat.create("Zed", 2L);
	}

	public static DuoSaveRequest 듀오_생성_요청() {
		return new DuoSaveRequest("summonerName", SummonerPosition.JUG, DESC);
	}

	public static DuoUpdateRequest 듀오_수정_요청() {
		return new DuoUpdateRequest(SummonerPosition.MID, DESC);
	}

	public static RecentMatchStatsDto 테스트_최근_전적() {
		return new RecentMatchStatsDto(LATEST_WIN_RATE, 테스트_챔피언_사용횟수());
	}

	public static DuoResponse 듀오_조회_응답() {
		List<DuoDto> content = List.of(
				DuoDto.create(테스트_듀오(테스트_회원(), SummonerPosition.JUG, SummonerTier.GOLD)),
				DuoDto.create(테스트_듀오(테스트_회원(), SummonerPosition.MID, SummonerTier.PLATINUM)),
				DuoDto.create(테스트_듀오(테스트_회원(), SummonerPosition.BOT, SummonerTier.GRANDMASTER)),
				DuoDto.create(테스트_듀오(테스트_회원(), SummonerPosition.TOP, SummonerTier.IRON))
		);

		Pageable pageable = PageRequest.of(0, 20);
		return new DuoResponse("11.16.0", 100L, content, pageable);
	}
}
