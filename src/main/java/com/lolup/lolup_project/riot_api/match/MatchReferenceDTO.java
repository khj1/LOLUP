package com.lolup.lolup_project.riot_api.match;

import lombok.Data;

@Data
public class MatchReferenceDTO {

    private Long gameId;
    private String role;
    private int season;
    private String platformId;
    private int champion;
    private int queue;
    private String lane;
    private Long timestamp;
    private String win;
}
