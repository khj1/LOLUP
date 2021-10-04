package com.lolup.lolup_project.riot_api.match;

import lombok.Data;

import java.util.List;

@Data
public class MatchInfoDto {
    private List<ParticipantDto> participants;
}
