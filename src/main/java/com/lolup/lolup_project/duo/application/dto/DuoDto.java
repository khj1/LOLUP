package com.lolup.lolup_project.duo.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.lolup.lolup_project.duo.domain.Duo;
import com.lolup.lolup_project.duo.domain.SummonerPosition;
import com.lolup.lolup_project.duo.domain.SummonerRank;
import com.lolup.lolup_project.duo.domain.SummonerTier;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DuoDto {

	private Long duoId;
	private Long memberId;
	private int iconId;
	private String summonerName;
	private SummonerPosition position;
	private SummonerTier tier;
	private SummonerRank rank;
	private List<MostInfoDto> most3;
	private int wins;
	private int losses;
	private double latestWinRate;
	private String desc;
	private LocalDateTime postDate;

	@Builder
	public DuoDto(final Long duoId, final Long memberId, final int iconId, final String summonerName,
				  final SummonerPosition position, final SummonerTier tier, final SummonerRank rank,
				  final List<MostInfoDto> most3,
				  final int wins, final int losses, final double latestWinRate, final String desc,
				  final LocalDateTime postDate) {
		this.duoId = duoId;
		this.memberId = memberId;
		this.iconId = iconId;
		this.summonerName = summonerName;
		this.position = position;
		this.tier = tier;
		this.rank = rank;
		this.most3 = most3;
		this.wins = wins;
		this.losses = losses;
		this.latestWinRate = latestWinRate;
		this.desc = desc;
		this.postDate = postDate;
	}

	public static DuoDto create(final Duo duo) {
		return DuoDto.builder()
				.duoId(duo.getId())
				.memberId(duo.getMember().getId())
				.iconId(duo.getInfo().getIconId())
				.summonerName(duo.getInfo().getSummonerName())
				.position(duo.getPosition())
				.tier(duo.getInfo().getTier())
				.rank(duo.getInfo().getRank())
				.most3(duo.getMostInfos().stream().map(MostInfoDto::create).collect(Collectors.toList()))
				.wins(duo.getInfo().getWins())
				.losses(duo.getInfo().getLosses())
				.latestWinRate(duo.getLatestWinRate())
				.desc(duo.getDesc())
				.postDate(duo.getCreatedDate())
				.build();
	}
}
