package com.lolup.lolup_project.duo;

import com.lolup.lolup_project.api.riot_api.summoner.SummonerPosition;
import com.lolup.lolup_project.api.riot_api.summoner.SummonerTier;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.*;

@AutoConfigureTestDatabase(replace = NONE)
@MybatisTest
class DuoServiceTest {

    @Autowired
    private DuoRepository duoRepository;


    @Test
    public void 듀오_추가_테스트() throws Exception {
        //given
        DuoDto duoDto = getDuoDto(SummonerTier.BRONZE, SummonerPosition.BOT);

        //when
        Long duoId = duoRepository.save(duoDto);

        //then
        DuoDto resultDto = duoRepository.findById(duoId);
        assertThat(duoDto.getDesc()).isEqualTo(resultDto.getDesc());
        assertThat(duoId).isEqualTo(resultDto.getDuoId());

    }

    @Test
    void 듀오_필터적용_조회() {
        // given
        ParameterDto parameter_gold_jug = ParameterDto.create(SummonerTier.GOLD, SummonerPosition.JUG);
        ParameterDto parameter_gold = ParameterDto.create(SummonerTier.GOLD, SummonerPosition.ALL);
        ParameterDto parameter_silver_jug = ParameterDto.create(SummonerTier.SILVER, SummonerPosition.JUG);
        ParameterDto parameter_silver = ParameterDto.create(SummonerTier.SILVER, SummonerPosition.ALL);
        ParameterDto parameter_all = ParameterDto.create(SummonerTier.ALL, SummonerPosition.ALL);

        // when
        duoRepository.save(getDuoDto(SummonerTier.GOLD, SummonerPosition.JUG));
        duoRepository.save(getDuoDto(SummonerTier.GOLD, SummonerPosition.TOP));
        duoRepository.save(getDuoDto(SummonerTier.SILVER, SummonerPosition.JUG));
        duoRepository.save(getDuoDto(SummonerTier.SILVER, SummonerPosition.TOP));

        List<DuoDto> list_gold_jug = duoRepository.findAll(parameter_gold_jug);
        List<DuoDto> list_gold = duoRepository.findAll(parameter_gold);
        List<DuoDto> list_all = duoRepository.findAll(parameter_all);

        int size_gold_jug = list_gold_jug.size();
        long count_gold_jug = list_gold_jug.stream()
                        .filter(dto ->
                                    dto.getPosition().equals(SummonerPosition.JUG) &&
                                    dto.getTier().equals(SummonerTier.GOLD)
                        )
                        .count();

        int size_gold = list_gold.size();
        long count_gold = list_gold.stream()
                    .filter(dto -> dto.getTier().equals(SummonerTier.GOLD))
                    .count();

        int size_all = list_all.size();

        //then
        assertThat(size_gold_jug).isEqualTo(count_gold_jug).isLessThan(size_all);
        assertThat(size_gold).isEqualTo(count_gold).isLessThan(size_all);
    }

    @Test
    void 데이터_수정() {
        //given
        Long beforeId = duoRepository.save(getDuoDto(SummonerTier.UNRANKED, SummonerPosition.MID));
        String desc = "updated";
        String position = SummonerPosition.BOT;

        //when
        duoRepository.update(beforeId, position, desc);
        DuoDto afterDto = duoRepository.findById(beforeId);

        //then
        assertThat(afterDto.getDesc()).isEqualTo("updated");
        assertThat(afterDto.getPosition()).isEqualTo(SummonerPosition.BOT);
    }


    @Test
    public void 데이터_삭제() throws Exception {
        //given
        DuoDto duoDto = getDuoDto(SummonerTier.UNRANKED, SummonerPosition.SUP);
        Long duoId = duoRepository.save(duoDto);

        //when
        duoRepository.delete(duoId);

        //then
        assertThat(duoId).isNotNull();
        assertThat(duoRepository.findById(duoId)).isEqualTo(null);
    }



    private DuoDto getDuoDto(String tier, String position) {
        Map<String, Integer> most3 = new HashMap<>();
        most3.put("Lucian", 2);
        most3.put("Syndra", 2);
        most3.put("Ryze", 2);

        return DuoDto.builder()
                .win(100)
                .summonerName("summonerName")
                .postDate(LocalDateTime.now())
                .tier(tier)
                .rank("3")
                .position(position)
                .most3(most3)
                .lose(100)
                .memberId(1L)
                .latestWinRate("20%")
                .desc("hi")
                .iconId(100)
                .build();
    }
}