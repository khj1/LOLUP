package com.lolup.lolup_project.duo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolup.lolup_project.api.riot_api.summoner.MostInfo;
import com.lolup.lolup_project.api.riot_api.summoner.SummonerDto;
import lombok.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class DuoDto {

    private Long duoId;
    private Long memberId;
    private int iconId;
    private String summonerName;
    private String position;
    private String tier;
    private String rank;
    private List<MostInfo> most3;
    private int wins;
    private int losses;
    private String latestWinRate;
    private String desc;
    private LocalDateTime postDate;

    @Builder
    public DuoDto(Long duoId, Long memberId, int iconId, String summonerName, String position,
                  String tier, String rank, List<MostInfo> most3, int wins, int losses,
                  String latestWinRate, String desc, LocalDateTime postDate) {

        this.duoId = duoId;
        this.memberId = memberId;
        this.iconId = iconId;
        this.summonerName = summonerName;
        this.position = position;
        this.tier = tier;
        this.rank = rank;
        this.most3 = most3;
        this.wins = wins;
        this.losses = losses;
        this.latestWinRate = latestWinRate;
        this.desc = desc;
        this.postDate = postDate;

    }

    public static DuoDto create(SummonerDto summonerDto, DuoForm form) {
        return DuoDto.builder()
                .desc(form.getDesc())
                .memberId(form.getMemberId())
                .position(form.getPosition())
                .postDate(form.getPostDate())
                .summonerName(form.getSummonerName())
                .most3(summonerDto.getMost3())
                .wins(summonerDto.getInfo().getWins())
                .rank(summonerDto.getInfo().getRank())
                .tier(summonerDto.getInfo().getTier())
                .losses(summonerDto.getInfo().getLosses())
                .latestWinRate(summonerDto.getLatestWinRate())
                .iconId(summonerDto.getInfo().getIconId())
                .build();
    }
}
