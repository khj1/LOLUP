package com.lolup.lolup_project.duo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolup.lolup_project.api.riot_api.summoner.SummonerDto;
import lombok.*;

import java.io.IOException;
import java.time.LocalDateTime;
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
    private Map<String, Integer> most3;
    private int win;
    private int lose;
    private String latestWinRate;
    private String desc;
    private LocalDateTime postDate;

    @Builder
    public DuoDto(Long duoId, Long memberId, int iconId, String summonerName, String position,
                  String tier, String rank, Map<String, Integer> most3, int win, int lose,
                  String latestWinRate, String desc, LocalDateTime postDate) {

        this.duoId = duoId;
        this.memberId = memberId;
        this.iconId = iconId;
        this.summonerName = summonerName;
        this.position = position;
        this.tier = tier;
        this.rank = rank;
        this.most3 = most3;
        this.win = win;
        this.lose = lose;
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
                .win(summonerDto.getInfo().getWin())
                .rank(summonerDto.getInfo().getRank())
                .tier(summonerDto.getInfo().getTier())
                .lose(summonerDto.getInfo().getLose())
                .latestWinRate(summonerDto.getLatestWinRate())
                .iconId(summonerDto.getInfo().getIconId())
                .build();
    }
}
