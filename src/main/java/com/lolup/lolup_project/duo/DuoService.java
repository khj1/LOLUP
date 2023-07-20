package com.lolup.lolup_project.duo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lolup.lolup_project.member.Member;
import com.lolup.lolup_project.member.MemberRepository;
import com.lolup.lolup_project.member.NoSuchMemberException;
import com.lolup.lolup_project.riotapi.RiotService;
import com.lolup.lolup_project.riotapi.riotstatic.RiotStaticService;
import com.lolup.lolup_project.riotapi.summoner.SummonerDto;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DuoService {

	private final MemberRepository memberRepository;
	private final DuoRepository duoRepository;
	private final RiotService riotService;
	private final RiotStaticService riotStaticService;

	public DuoResponse findAll(final String position, final String tier, final Pageable pageable) {
		Page<DuoDto> data = duoRepository.findAll(position, tier, pageable);

		return new DuoResponse(data, riotStaticService.getLatestGameVersion());
	}

	@Transactional
	public void save(final Long memberId, final DuoSaveRequest request) {
		SummonerDto summonerDto = riotService.find(request.getSummonerName());
		Member member = memberRepository.findById(memberId)
				.orElseThrow(NoSuchMemberException::new);

		Duo duo = Duo.create(member, summonerDto, request.getPosition(), request.getDesc());
		duoRepository.save(duo);
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
