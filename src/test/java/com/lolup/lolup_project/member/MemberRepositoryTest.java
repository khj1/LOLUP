package com.lolup.lolup_project.member;

import com.lolup.lolup_project.config.JasyptConfig;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = NONE)
@EnableEncryptableProperties
@MybatisTest
@Import(JasyptConfig.class)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 중복은_업데이트() throws Exception {
        //given
        Member member1 = Member.builder()
                .name("김떙땡")
                .picture(null)
                .role(Role.USER)
                .email("my@email.com")
                .summonerName(null)
                .build();

        //when
        String email1 = memberRepository.save(member1);

        Member member2 = Member.builder()
                .name("김띵띵")
                .picture(null)
                .role(Role.USER)
                .email(email1)
                .summonerName("한준")
                .build();

        String email2 = memberRepository.save(member2);

        //then
        String resultName = memberRepository.findByEmail(email1).get().getName();

        assertThat(email1).isEqualTo(email2);
        assertThat(resultName).isEqualTo("김띵띵");

    }
}