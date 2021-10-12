package com.lolup.lolup_project.riotapi.match;

import lombok.Data;

@Data
public class ParticipantDto {
    private String championName;
    private String summonerName;
    private int championId;
    private int teamId;
    private boolean win;
}
