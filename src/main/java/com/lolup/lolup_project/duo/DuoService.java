package com.lolup.lolup_project.duo;

import com.lolup.lolup_project.api.riot_api.summoner.SummonerDto;
import com.lolup.lolup_project.api.riot_api.summoner.SummonerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class DuoService {

    private final DuoRepository duoRepository;
    private final SummonerService summonerService;

    public Map<String, Object> findAll(String position, String tier, int section) {
        List<DuoDto> data = duoRepository.findAll(position, tier, (section * 20));

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("version", summonerService.getGameVersion());
        map.put("totalCount", duoRepository.getTotalCount());
        map.put("data", data);

        return map;
    }

    public DuoDto findById(Long duoId) {
        return duoRepository.findById(duoId);
    }


    public Long save(DuoForm form) {
        SummonerDto summonerDto = summonerService.find(form.getSummonerName());
        DuoDto duoDto = DuoDto.create(summonerDto, form);
        return duoRepository.save(duoDto);
    }


    public Long update(Long duoId, String position, String desc) {
        return duoRepository.update(duoId, position, desc);
    }

    public Long delete(Long duoId, Long memberId) {
        return duoRepository.delete(duoId, memberId);
    }
}
