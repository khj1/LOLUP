package com.lolup.lolup_project.api.riot_api.summoner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(value = SummonerController.class, excludeAutoConfiguration = ReactiveSecurityAutoConfiguration.class)
class SummonerControllerTest {

    @Autowired
    WebTestClient webClient;

    @MockBean
    SummonerService service;

    @Test
    public void 소환사정보_정상조회_테스트() throws Exception {
        SummonerRankDto rankDto = SummonerRankDto.builder()
                .summonerName("name")
                .tier("BRONZE")
                .rank("3")
                .profileIconId(300)
                .wins(20)
                .losses(20)
                .build();

        Map<String, Integer> map = new HashMap<>();
        map.put("zed", 3);
        map.put("ziggs", 2);
        map.put("lucian", 2);

        SummonerDto Summonerdto = SummonerDto.builder()
                .info(rankDto)
                .latestWinRate("10%")
                .version("1")
                .most3(map)
                .build();

        when(service.find("correctName")).thenReturn(Summonerdto);

        webClient.get().uri("/riot/correctName")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.version").isEqualTo("1")
                .jsonPath("$.latestWinRate").isEqualTo("10%")
                .jsonPath("$.info.summonerName").isEqualTo("name")
                .jsonPath("$.info.tier").isEqualTo("BRONZE")
                .jsonPath("$.info.rank").isEqualTo("3")
                .jsonPath("$.info.profileIconId").isEqualTo(300)
                .jsonPath("$.info.wins").isEqualTo(20)
                .jsonPath("$.info.losses").isEqualTo(20)
                .jsonPath("$.most3").isNotEmpty();
    }

    @Test
    public void 잘못된_소환사_이름_조회() throws Exception {
        //when
        when(service.find("wrongName")).thenThrow(WebClientResponseException.class);

        //then
        webClient.get().uri("/riot/wrongName")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.code").isEqualTo("NOT FOUND")
                .jsonPath("$.message").isEqualTo("해당 소환사가 존재하지 않습니다.");

    }
}