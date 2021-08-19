package com.lolup.lolup_project.duo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class DuoForm {
    private Long duoId;
    private Long memberId;
    private String summonerName;
    private String position;
    private String desc;
    private LocalDateTime postDate;

    @Builder
    public DuoForm(Long duoId, Long memberId, String summonerName, String position, String desc, LocalDateTime postDate) {
        this.duoId = duoId;
        this.memberId = memberId;
        this.summonerName = summonerName;
        this.position = position;
        this.desc = desc;
        this.postDate = postDate;
    }
}
