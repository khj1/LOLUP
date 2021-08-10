package com.lolup.lolup_project.riot_api.summoner;

import lombok.Data;

@Data
public class SummonerAccountDTO {

    private String accountId;
    private int profileIconId;
    private Long revisionDate; // Date summoner was last modified
    private String name;
    private String id;
    private String puuid;
    private Long summonerLevel;

}
