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
    private int win;
    private int lose;

    @Builder
    public SummonerRankDto(int iconId, String summonerName, String tier, String rank, int win, int lose) {
        this.iconId = iconId;
        this.summonerName = summonerName;
        this.tier = tier;
        this.rank = rank;
        this.win = win;
        this.lose = lose;
    }
}
