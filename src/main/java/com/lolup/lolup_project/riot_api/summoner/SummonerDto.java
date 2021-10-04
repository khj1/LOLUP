package com.lolup.lolup_project.riot_api.summoner;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
public class SummonerDto {

    private String latestWinRate;
    private SummonerRankInfo info;
    private List<MostInfo> most3;

    public SummonerDto(String latestWinRate, SummonerRankInfo info, List<MostInfo> most3) {
        this.latestWinRate = latestWinRate;
        this.info = info;
        this.most3 = most3;
    }
}
