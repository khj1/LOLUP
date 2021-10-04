package com.lolup.lolup_project.member;

import com.github.gavlyukovskiy.boot.jdbc.decorator.DataSourceDecoratorAutoConfiguration;
import com.lolup.lolup_project.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest(showSql = false)
@Import(TestConfig.class)
@ImportAutoConfiguration(DataSourceDecoratorAutoConfiguration.class)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 중복은_업데이트() throws Exception {
        //given
        String email = "my@email.com";

        Member member1 = Member.builder()
                .name("김떙땡")
                .picture(null)
                .role(Role.USER)
                .email("my@email.com")
                .summonerName(null)
                .build();

        //when
        memberRepository.save(member1);

        Member member2 = Member.builder()
                .id(member1.getId())
                .name("김띵띵")
                .picture(null)
                .role(Role.USER)
                .email("my@email.com")
                .summonerName(null)
                .build();

        memberRepository.save(member2);

        //then
        String resultName = memberRepository.findByEmail(email).orElse(null).getName();

        assertThat(resultName).isEqualTo("김띵띵");

    }
}