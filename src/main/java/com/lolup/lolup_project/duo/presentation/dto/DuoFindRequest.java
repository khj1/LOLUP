package com.lolup.lolup_project.duo.presentation.dto;

import org.springframework.data.domain.Pageable;

import com.lolup.lolup_project.duo.domain.SummonerPosition;
import com.lolup.lolup_project.duo.domain.SummonerTier;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DuoFindRequest {

	private SummonerPosition position;
	private SummonerTier tier;
	private Pageable pageable;

	public DuoFindRequest(final SummonerPosition position, final SummonerTier tier, final Pageable pageable) {
		this.position = position;
		this.tier = tier;
		this.pageable = pageable;
	}
}
