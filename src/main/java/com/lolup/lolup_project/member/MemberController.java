package com.lolup.lolup_project.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;
@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PatchMapping("/member/{memberId}")
    public ResponseEntity<Map<String, Object>> changeSummonerName(Long memberId, String summonerName){

        log.info("memberController 호출");
        Map<String, Object> map = memberService.updateSummonerName(memberId, summonerName);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
