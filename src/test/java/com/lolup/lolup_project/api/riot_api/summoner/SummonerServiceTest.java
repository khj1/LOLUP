package com.lolup.lolup_project.api.riot_api.summoner;

import com.lolup.lolup_project.api.riot_api.match.MatchReferenceDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.webservices.client.WebServiceClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class SummonerServiceTest {

    @Autowired
    SummonerService summonerService;

    @Test
    void 최근_10게임_모스트3_추출() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getLatestMatches = summonerService.getClass().getDeclaredMethod("getLatestMatches", String.class);
        Method getLatestMost3 = summonerService.getClass().getDeclaredMethod("getLatestMost3", List.class);

        getLatestMatches.setAccessible(true);
        getLatestMost3.setAccessible(true);

        List<MatchReferenceDTO> matches = (List<MatchReferenceDTO>) getLatestMatches.invoke(summonerService, "hideonbush");
        List<MostInfo> most3 = (List<MostInfo>) getLatestMost3.invoke(summonerService, matches);

        assertThat(most3.size()).isEqualTo(3);
        assertThat(most3.get(0).getPlay()).isGreaterThanOrEqualTo(most3.get(1).getPlay());
        assertThat(most3.get(1).getPlay()).isGreaterThanOrEqualTo(most3.get(2).getPlay());
    }
}