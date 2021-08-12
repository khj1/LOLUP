package com.lolup.lolup_project.api.riot_api.summoner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class SummonerController {

    private final SummonerService summonerService;

    @GetMapping("/summoner/account/{summonerName}")
    public String getRankInfo(@PathVariable String summonerName, Model model) {
        model.addAttribute("info", summonerService.getSummonerTotalSoloRankInfo(summonerName));
        model.addAttribute("version", summonerService.getGameVersion()[0]);
        return "summonerInfo";
    }

    @GetMapping("/summoner/match/{summonerName}")
    public String getLatestSoloRankHistories(@PathVariable String summonerName, Model model) {
        Map<String, Object> map = summonerService.getSummonerSummaryInfo(summonerName);
        model.addAttribute("version", map.get("version"));
        model.addAttribute("totalInfo", map.get("totalInfo"));
        model.addAttribute("latestWinRate", map.get("winRateOfLatestGames"));
        model.addAttribute("most3", map.get("most3ChampOfLatestGames"));

        return "summonerInfo";
    }

}
