package com.lolup.lolup_project.api.riot_api.summoner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SummonerController {

    private final SummonerService summonerService;

    @GetMapping("/riot/{summonerName}")
    public Map<String, Object> getLatestSoloRankHistories(@PathVariable String summonerName) {
        return summonerService.getSummonerSummaryInfo(summonerName);
    }
}
