package com.lolup.lolup_project.api.riot_api.summoner;

import lombok.Data;

@Data
public class SummonerAccountDto {

    private String accountId;
    private int IconId;
    private Long revisionDate; // Date summoner was last modified
    private String name;
    private String id;
    private String puuid;
    private Long summonerLevel;

}
