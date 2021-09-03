package com.lolup.lolup_project.api.riot_api.summoner;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class SummonerRankDto {

    private int iconId;
    private String summonerName;
    private String tier;
    private String rank;
    private int wins;
    private int losses;

    @Builder
    public SummonerRankDto(int iconId, String summonerName, String tier, String rank, int wins, int losses) {
        this.iconId = iconId;
        this.summonerName = summonerName;
        this.tier = tier;
        this.rank = rank;
        this.wins = wins;
        this.losses = losses;
    }
}
