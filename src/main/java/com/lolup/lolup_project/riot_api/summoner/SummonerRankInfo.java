package com.lolup.lolup_project.riot_api.summoner;

import lombok.*;

import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class SummonerRankInfo {

    private int iconId;
    private String summonerName;
    private String tier;
    private String rank;
    private int wins;
    private int losses;

    @Builder
    public SummonerRankInfo(int iconId, String summonerName, String tier, String rank, int wins, int losses) {
        this.iconId = iconId;
        this.summonerName = summonerName;
        this.tier = tier;
        this.rank = rank;
        this.wins = wins;
        this.losses = losses;
    }
}
