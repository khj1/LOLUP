package com.lolup.member.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lolup.member.domain.Member;
import com.lolup.member.domain.MemberRepository;
import com.lolup.member.exception.NoSuchMemberException;
import com.lolup.riot.summoner.application.SummonerService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final SummonerService summonerService;

	@Transactional
	public void updateSummonerName(final Long memberId, final String summonerName) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(NoSuchMemberException::new);

		validateSummonerName(summonerName);
		member.changeSummonerName(summonerName);
	}

	private void validateSummonerName(final String summonerName) {
		summonerService.requestAccountInfo(summonerName);
	}
}
