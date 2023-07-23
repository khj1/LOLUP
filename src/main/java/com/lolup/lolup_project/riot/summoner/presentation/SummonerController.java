package com.lolup.lolup_project.riot.summoner.presentation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.lolup.lolup_project.riot.summoner.application.SummonerService;
import com.lolup.lolup_project.riot.summoner.application.dto.SummonerAccountDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
