package com.lolup.lolup_project.api.riot_api.summoner;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SummonerAccountDto {

    private String accountId;
    private int IconId;
    private Long revisionDate; // Date summoner was last modified
    private String name;
    private String id;
    private String puuid;
    private Long summonerLevel;

    @Builder
    public SummonerAccountDto(String accountId, int iconId, Long revisionDate, String name, String id, String puuid, Long summonerLevel) {
        this.accountId = accountId;
        IconId = iconId;
        this.revisionDate = revisionDate;
        this.name = name;
        this.id = id;
        this.puuid = puuid;
        this.summonerLevel = summonerLevel;
    }
}
