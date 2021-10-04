package com.lolup.lolup_project.duo;

import com.lolup.lolup_project.riot_api.summoner.MostInfo;
import com.lolup.lolup_project.riot_api.summoner.MostInfoDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class DuoDto {
    private Long duoId;
    private Long memberId;
    private int iconId;
    private String summonerName;
    private String position;
    private String tier;
    private String rank;
    private List<MostInfoDto> most3;
    private int wins;
    private int losses;
    private String latestWinRate;
    private String desc;
    private LocalDateTime postDate;

    @Builder
    public DuoDto(Long duoId, Long memberId, int iconId, String summonerName, String position, String tier, String rank, List<MostInfoDto> most3, int wins, int losses, String latestWinRate, String desc, LocalDateTime postDate) {
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

    public static DuoDto create(Duo duo) {
        return DuoDto.builder()
                .duoId(duo.getId())
                .memberId(duo.getMember().getId())
                .iconId(duo.getInfo().getIconId())
                .summonerName(duo.getInfo().getSummonerName())
                .position(duo.getPosition())
                .tier(duo.getInfo().getTier())
                .rank(duo.getInfo().getRank())
                .most3(duo.getMostInfos().stream().map(MostInfoDto::create).collect(Collectors.toList()))
                .wins(duo.getInfo().getWins())
                .losses(duo.getInfo().getLosses())
                .latestWinRate(duo.getLatestWinRate())
                .desc(duo.getDesc())
                .postDate(duo.getCreatedDate())
                .build();
    }
}
