package com.lolup.lolup_project.riotapi.summoner;

import lombok.Data;

@Data
public class MostInfoDto {
    private String name;
    private Integer play;

    public MostInfoDto(String name, Integer play) {
        this.name = name;
        this.play = play;
    }

    public static MostInfoDto create(MostInfo mostInfo) {
        return new MostInfoDto(mostInfo.getName(), mostInfo.getPlay());
    }
}
