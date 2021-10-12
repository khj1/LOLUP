package com.lolup.lolup_project.riotapi.summoner;

import com.lolup.lolup_project.base.BaseTimeEntity;
import com.lolup.lolup_project.duo.Duo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MostInfo extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "most_info_id")
    private Long id;

    private String name;
    private Integer play;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "duo_id")
    private Duo duo;

    public MostInfo(String name, Integer play) {
        this.name = name;
        this.play = play;
    }

    public static MostInfo create(String name, Integer play) {
        return new MostInfo(name, play);
    }

    public void changeDuo(Duo duo) {
        this.duo = duo;
    }
}
