package com.lolup.lolup_project.duo;

import com.lolup.lolup_project.api.riot_api.summoner.SummonerDto;
import com.lolup.lolup_project.api.riot_api.summoner.SummonerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DuoService {

    private final DuoRepository duoRepository;
    private final SummonerService summonerService;


    public List<DuoDto> findAll(ParameterDto parameterDto) {
        return duoRepository.findAll(parameterDto);
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

    public Long delete(Long duoId) {
        return duoRepository.delete(duoId);
    }
}
