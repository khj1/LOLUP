package com.lolup.lolup_project.duo;

import com.lolup.lolup_project.base.BaseTimeEntity;
import com.lolup.lolup_project.member.Member;
import com.lolup.lolup_project.riotapi.summoner.MostInfo;
import com.lolup.lolup_project.riotapi.summoner.SummonerDto;
import com.lolup.lolup_project.riotapi.summoner.SummonerRankInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Duo extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "duo_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Embedded
    private SummonerRankInfo info;

    @OneToMany(mappedBy = "duo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MostInfo> mostInfos = new ArrayList<>();

    private String position;
    private String latestWinRate;
    private String desc;

    public Duo(Member member, SummonerRankInfo info, List<MostInfo> mostInfos, String position, String latestWinRate, String desc) {
        this.member = member;
        this.info = info;
        this.mostInfos = mostInfos;
        this.position = position;
        this.latestWinRate = latestWinRate;
        this.desc = desc;
    }

    public static Duo create(Member member, SummonerDto summonerDto, String position, String desc) {
        return new Duo(member, summonerDto.getInfo(), summonerDto.getMost3(), position, summonerDto.getLatestWinRate(), desc);
    }
}
