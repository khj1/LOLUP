package com.lolup.lolup_project.api.riot_api.summoner;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MostInfo {
    private String name;
    private Integer play;

    public static MostInfo create(String name, Integer play) {
        MostInfo mostInfo = new MostInfo();
        mostInfo.setName(name);
        mostInfo.setPlay(play);

        return mostInfo;
    }
}
