package com.lolup.lolup_project.api.riot_api.summoner;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class SummonerRankDto {

    private int profileIconId;
    private String summonerName;
    private String tier;
    private String rank;
    private int wins;
    private int losses;

    @Builder
    public SummonerRankDto(int profileIconId, String summonerName, String tier, String rank, int wins, int losses) {
        this.profileIconId = profileIconId;
        this.summonerName = summonerName;
        this.tier = tier;
        this.rank = rank;
        this.wins = wins;
        this.losses = losses;
    }
}
