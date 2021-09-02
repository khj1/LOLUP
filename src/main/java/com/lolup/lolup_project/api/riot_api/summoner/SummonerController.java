package com.lolup.lolup_project.api.riot_api.summoner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SummonerController {

    private final SummonerService summonerService;

    @GetMapping("/riot/find/{summonerName}")
    public ResponseEntity<Map<String, Object>> find(@PathVariable String summonerName) {
        Map<String, Object> map = new HashMap<>();

        SummonerAccountDto accountInfo = summonerService.getAccountInfo(summonerName);
        if (accountInfo.getName() != null) {
            map.put("summonerName", accountInfo.getName());
        }

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

}
