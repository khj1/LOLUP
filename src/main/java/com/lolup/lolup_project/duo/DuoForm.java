package com.lolup.lolup_project.duo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DuoForm {
    private Long memberId;
    private String summonerName;
    private String position;
    private String desc;
    private LocalDateTime postDate;
}
