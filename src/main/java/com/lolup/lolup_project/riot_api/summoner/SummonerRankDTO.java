package com.lolup.lolup_project.riot_api.summoner;

import lombok.Data;

@Data
public class SummonerRankDTO {

    private Long summonerLevel;
    private int profileIconId;
    private String leagueId;
    private String summonerId;
    private String summonerName;
    private String queueType;
    private String tier;
    private String rank;
    private int leaguePoints;
    private int wins;
    private int losses;
    private boolean hotStreak;
    private boolean veteran;
    private boolean freshBlood;
    private boolean inactive;
}
