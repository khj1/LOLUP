package com.lolup.lolup_project.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Map<String, Object> updateSummonerName(Long memberId, String summonerName) {
        Map<String, Object> map = new HashMap<>();

        if (memberRepository.update(memberId, summonerName) == 1) {
            map.put("updatedSummonerName", summonerName);
        } else {
            throw new RuntimeException("소환사 이름 업데이트 실패");
        }

        return map;
    }
}
