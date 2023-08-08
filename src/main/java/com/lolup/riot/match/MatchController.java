package com.lolup.riot.match;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lolup.riot.match.application.MatchService;
import com.lolup.riot.match.application.dto.RecentMatchStatsDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MatchController {

	private final MatchService matchService;

	@GetMapping("/match")
	public ResponseEntity<RecentMatchStatsDto> getRecentMatchStats(final String puuId) {
		RecentMatchStatsDto recentMatchStats = matchService.requestRecentMatchStats(puuId);

		return ResponseEntity.ok().body(recentMatchStats);
	}
}
