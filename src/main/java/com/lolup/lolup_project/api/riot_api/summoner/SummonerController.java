package com.lolup.lolup_project.api.riot_api.summoner;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<SummonerDto> find(@PathVariable String summonerName) {
        return new ResponseEntity<SummonerDto>(summonerService.find(summonerName), HttpStatus.OK);
    }
}
