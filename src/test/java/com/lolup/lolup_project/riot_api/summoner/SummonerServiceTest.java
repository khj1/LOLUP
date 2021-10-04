package com.lolup.lolup_project.riot_api.summoner;

import com.lolup.lolup_project.riot_api.match.MatchInfoDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SummonerServiceTest {

    @Autowired
    SummonerService summonerService;

    @Test
    void 최근_10게임_모스트3_추출() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getMatchInfos = summonerService.getClass().getDeclaredMethod("getMatchInfos", String.class);
        Method getLatestMost3 = summonerService.getClass().getDeclaredMethod("getLatestMost3", String.class, List.class);

        getMatchInfos.setAccessible(true);
        getLatestMost3.setAccessible(true);

        List<MatchInfoDto> matchInfoDtos = (List<MatchInfoDto>) getMatchInfos.invoke(summonerService, "Hide on bush");
        List<MostInfo> most3 = (List<MostInfo>) getLatestMost3.invoke(summonerService, "Hide on bush", matchInfoDtos);

        assertThat(most3.size()).isEqualTo(3);
        assertThat(most3.get(0).getPlay()).isGreaterThanOrEqualTo(most3.get(1).getPlay());
        assertThat(most3.get(1).getPlay()).isGreaterThanOrEqualTo(most3.get(2).getPlay());
    }
}