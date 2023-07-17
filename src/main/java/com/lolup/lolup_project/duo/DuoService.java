package com.lolup.lolup_project.duo;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lolup.lolup_project.member.Member;
import com.lolup.lolup_project.member.MemberRepository;
import com.lolup.lolup_project.member.NoSuchMemberException;
import com.lolup.lolup_project.riotapi.summoner.SummonerDto;
import com.lolup.lolup_project.riotapi.summoner.SummonerService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DuoService {

	private final MemberRepository memberRepository;
	private final DuoRepository duoRepository;
	private final SummonerService summonerService;

	public Map<String, Object> findAll(String position, String tier, Pageable pageable) {
		Page<DuoDto> data = duoRepository.findAll(position, tier, pageable);

		Map<String, Object> map = new LinkedHashMap<>();
		map.put("version", summonerService.getGameVersion());
		map.put("totalCount", data.getTotalElements());
		map.put("data", data.getContent());
		map.put("pageable", data.getPageable());

		return map;
	}

	@Transactional
	public void save(final Long memberId, final DuoSaveRequest request) {
		SummonerDto summonerDto = summonerService.find(request.getSummonerName());
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
	public Long delete(Long duoId, Long memberId) {
		duoRepository.deleteByIdAndMemberId(duoId, memberId);

		return duoId;
	}
}
