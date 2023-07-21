package com.lolup.lolup_project.riotapi.match;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchDto {

    private MatchInfoDto info;

    public MatchDto(final MatchInfoDto info) {
        this.info = info;
    }
}
