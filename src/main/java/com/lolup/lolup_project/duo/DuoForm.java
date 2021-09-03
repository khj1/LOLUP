package com.lolup.lolup_project.duo;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class DuoForm {
    private Long memberId;
    private String summonerName;
    private String position;
    private String desc;
    private LocalDateTime postDate;

    @Builder
    public DuoForm(Long memberId, String summonerName, String position, String desc, LocalDateTime postDate) {
        this.memberId = memberId;
        this.summonerName = summonerName;
        this.position = position;
        this.desc = desc;
        this.postDate = postDate;
    }
}
