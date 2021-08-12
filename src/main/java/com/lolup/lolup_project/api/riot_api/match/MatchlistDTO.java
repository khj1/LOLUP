package com.lolup.lolup_project.api.riot_api.match;

import lombok.Data;

import java.util.List;

@Data
public class MatchlistDTO {

    private int startIndex;
    private int totalGames;
    private int endIndex;
    private List<MatchReferenceDTO> matches;
}
