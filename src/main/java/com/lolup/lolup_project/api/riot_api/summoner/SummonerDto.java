package com.lolup.lolup_project.api.riot_api.summoner;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class SummonerDto {

    private String latestWinRate;
    private SummonerRankDto info;
    private List<MostInfo> most3;

    @Builder
    public SummonerDto(String latestWinRate, SummonerRankDto info, List<MostInfo> most3) {
        this.latestWinRate = latestWinRate;
        this.info = info;
        this.most3 = most3;
    }
}
