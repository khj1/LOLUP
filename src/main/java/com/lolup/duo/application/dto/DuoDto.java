package com.lolup.duo.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.lolup.duo.domain.Duo;
import com.lolup.duo.domain.SummonerPosition;
import com.lolup.duo.domain.SummonerStat;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//TODO 필드가 너무 많은 것 같다. 수정 필요
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DuoDto {

	private Long duoId;
	private Long memberId;
	private SummonerStat summonerStat;
	private List<ChampionStatDto> championStats;
	private SummonerPosition position;
	private int profileIconId;
	private double latestWinRate;
	private String description;
	private LocalDateTime createdDate;

	@Builder
	public DuoDto(final Long duoId, final Long memberId, final SummonerStat summonerStat,
				  final List<ChampionStatDto> championStats, final SummonerPosition position, final int profileIconId,
				  final double latestWinRate, final String description, final LocalDateTime createdDate) {
		this.duoId = duoId;
		this.memberId = memberId;
		this.summonerStat = summonerStat;
		this.championStats = championStats;
		this.position = position;
		this.profileIconId = profileIconId;
		this.latestWinRate = latestWinRate;
		this.description = description;
		this.createdDate = createdDate;
	}

	public static DuoDto create(final Duo duo) {
		return DuoDto.builder()
				.duoId(duo.getId())
				.memberId(duo.getMember().getId())
				.profileIconId(duo.getProfileIconId())
				.championStats(
						duo.getChampionStats().stream()
								.map(ChampionStatDto::create)
								.collect(Collectors.toList())
				)
				.summonerStat(duo.getSummonerStat())
				.description(duo.getDesc())
				.createdDate(duo.getCreatedDate())
				.build();
	}
}
