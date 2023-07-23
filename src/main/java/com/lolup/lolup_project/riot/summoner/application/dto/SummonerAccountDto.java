package com.lolup.lolup_project.riot.summoner.application.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SummonerAccountDto {

	private String accountId;
	private int profileIconId;
	private Long revisionDate; //소환사 정보가 갱신된 날짜
	private String name;
	private String id;
	private String puuid;
	private Long summonerLevel;

	@Builder
	public SummonerAccountDto(final String accountId, final int profileIconId, final Long revisionDate,
							  final String name, final String id, final String puuid, final Long summonerLevel) {
		this.accountId = accountId;
		this.profileIconId = profileIconId;
		this.revisionDate = revisionDate;
		this.name = name;
		this.id = id;
		this.puuid = puuid;
		this.summonerLevel = summonerLevel;
	}
}
