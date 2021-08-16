package com.lolup.lolup_project.api.riot_api.summoner;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SummonerRankDTO {

    private int profileIconId;
    private String summonerName;
    private String tier;
    private String rank;
    private int wins;
    private int losses;

}
