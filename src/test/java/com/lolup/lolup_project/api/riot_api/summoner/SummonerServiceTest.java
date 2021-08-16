package com.lolup.lolup_project.api.riot_api.summoner;

import com.lolup.lolup_project.api.riot_api.match.MatchReferenceDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SummonerServiceTest {

    @Autowired
    SummonerService summonerService;

    @Test
    void 최근_10게임_모스트3_추출() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<MatchReferenceDTO> matches = summonerService.getLatestMatches("hideonbush");
        Method method = summonerService.getClass().getDeclaredMethod("getLatestMost3", List.class);
        method.setAccessible(true);

        Map<String, Integer> most3 = (Map<String, Integer>) method.invoke(summonerService, matches);
        List<Map.Entry<String, Integer>> entries = most3.entrySet().stream().collect(Collectors.toList());

        assertThat(most3.size()).isEqualTo(3);
        assertThat(entries.get(0).getValue()).isGreaterThanOrEqualTo(entries.get(1).getValue());
        assertThat(entries.get(1).getValue()).isGreaterThanOrEqualTo(entries.get(2).getValue());
    }

}