package com.lolup.lolup_project.riot_api.match;

import lombok.Data;

@Data
public class ParticipantDto {
    private String championName;
    private String summonerName;
    private int championId;
    private int teamId;
    private boolean win;
}
