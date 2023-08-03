package com.lolup.riot.summoner.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SummonerAccountDto {

	private String accountId;
	private int profileIconId;
	private Long revisionDate; //소환사 정보가 갱신된 날짜
	private String name;
	private String id;
	private String puuid;
	private Long summonerLevel;
}
