package com.lolup.lolup_project.duo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParameterDto {
    private String tier;
    private String position;

    public static ParameterDto create(String tier, String position) {
        ParameterDto parameterDto = new ParameterDto();
        parameterDto.setPosition(position);
        parameterDto.setTier(tier);
        return parameterDto;
    }
}
