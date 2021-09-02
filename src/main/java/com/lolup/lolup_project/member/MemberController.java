package com.lolup.lolup_project.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PatchMapping("/member/{memberId}")
    public ResponseEntity<Map<String, Object>> find(Long memberId, String summonerName){
        Map<String, Object> map = memberService.updateSummonerName(memberId, summonerName);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
