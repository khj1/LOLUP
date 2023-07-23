package com.lolup.lolup_project.duo.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lolup.lolup_project.duo.application.dto.DuoDto;
import com.lolup.lolup_project.duo.application.dto.DuoResponse;
import com.lolup.lolup_project.duo.application.dto.DuoSaveRequest;
import com.lolup.lolup_project.duo.domain.Duo;
import com.lolup.lolup_project.duo.domain.DuoRepository;
import com.lolup.lolup_project.duo.exception.NoSuchDuoException;
import com.lolup.lolup_project.member.domain.Member;
import com.lolup.lolup_project.member.domain.MemberRepository;
import com.lolup.lolup_project.member.exception.NoSuchMemberException;
import com.lolup.lolup_project.riotapi.match.MatchService;
import com.lolup.lolup_project.riotapi.match.RecentMatchStatsDto;
import com.lolup.lolup_project.riotapi.riotstatic.RiotStaticService;
import com.lolup.lolup_project.riotapi.summoner.SummonerAccountDto;
import com.lolup.lolup_project.riotapi.summoner.SummonerDto;
import com.lolup.lolup_project.riotapi.summoner.SummonerRankInfo;
import com.lolup.lolup_project.riotapi.summoner.SummonerService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DuoService {

	private final MatchService matchService;
	private final SummonerService summonerService;
	private final RiotStaticService riotStaticService;
	private final DuoRepository duoRepository;
	private final MemberRepository memberRepository;

	public DuoResponse findAll(final String position, final String tier, final Pageable pageable) {
		Page<DuoDto> data = duoRepository.findAll(position, tier, pageable);

		return new DuoResponse(data, riotStaticService.getLatestGameVersion());
	}

	@Transactional
	public void save(final Long memberId, final DuoSaveRequest request) {
		SummonerDto summonerDto = find(request.getSummonerName());
		Member member = memberRepository.findById(memberId)
				.orElseThrow(NoSuchMemberException::new);

		Duo duo = Duo.create(member, summonerDto, request.getPosition(), request.getDesc());
		duoRepository.save(duo);
	}

	private SummonerDto find(final String summonerName) {
		SummonerAccountDto accountDto = summonerService.getAccountInfo(summonerName);
		SummonerRankInfo info = summonerService.getSummonerTotalSoloRankInfo(accountDto.getId(), accountDto.getName());
		RecentMatchStatsDto recentMatchStats = matchService.getRecentMatchStats(summonerName, accountDto.getPuuid());

		return new SummonerDto(accountDto.getProfileIconId(), recentMatchStats.getLatestWinRate(), info,
				recentMatchStats.getMost3());
	}

	@Transactional
	public void update(final Long duoId, final String position, final String desc) {
		Duo duo = duoRepository.findById(duoId)
				.orElseThrow(NoSuchDuoException::new);

		duo.update(position, desc);
	}

	@Transactional
	public void delete(final Long duoId, final Long memberId) {
		duoRepository.deleteByIdAndMemberId(duoId, memberId);
	}
}
