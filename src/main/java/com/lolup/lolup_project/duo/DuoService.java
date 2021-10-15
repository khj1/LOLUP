package com.lolup.lolup_project.duo;

import com.lolup.lolup_project.member.Member;
import com.lolup.lolup_project.member.MemberRepository;
import com.lolup.lolup_project.riotapi.summoner.SummonerDto;
import com.lolup.lolup_project.riotapi.summoner.SummonerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DuoService {

    private final MemberRepository memberRepository;
    private final DuoRepository duoRepository;
    private final SummonerService summonerService;

    public Map<String, Object> findAll(String position, String tier, Pageable pageable) {
        Page<DuoDto> data = duoRepository.findAll(position, tier, pageable);

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("version", summonerService.getGameVersion());
        map.put("totalCount", data.getTotalElements());
        map.put("data", data.getContent());
        map.put("pageable", data.getPageable());

        return map;
    }

    @Transactional
    public Long save(DuoForm form) {
        SummonerDto summonerDto = summonerService.find(form.getSummonerName());
        Member member = memberRepository.findById(form.getMemberId()).orElse(null);
        Duo duo = Duo.create(member, summonerDto, form.getPosition(), form.getDesc());

        return duoRepository.save(duo).getId();
    }


    @Transactional
    public Long update(Long duoId, String position, String desc) {
        Duo findDuo = duoRepository.findById(duoId).orElse(null);
        findDuo.update(position, desc);

        return duoId;
    }

    @Transactional
    public Long delete(Long duoId, Long memberId) {
        duoRepository.deleteByIdAndMemberId(duoId, memberId);

        return duoId;
    }
}
