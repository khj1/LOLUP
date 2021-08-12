package com.lolup.lolup_project.api.riot_api.summoner;

import com.lolup.lolup_project.api.riot_api.match.MatchReferenceDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SummonerServiceTest {

    @Autowired
    SummonerService summonerService;

    @Test
    void getMost3ChampOfLatestGames() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<MatchReferenceDTO> matches = summonerService.getLatestSoloRankHistories("hideonbush");
        Method method = summonerService.getClass().getDeclaredMethod("getMost3ChampOfLatestGames", List.class);
        method.setAccessible(true);

        List<Map.Entry<String, Integer>> most3
                = (List<Map.Entry<String, Integer>>) method.invoke(summonerService, matches);

        System.out.println("most3 = " + most3);

        assertThat(most3.size()).isEqualTo(3);
        assertThat(most3.get(0).getValue()).isGreaterThanOrEqualTo(most3.get(1).getValue());
        assertThat(most3.get(1).getValue()).isGreaterThanOrEqualTo(most3.get(2).getValue());
    }

}