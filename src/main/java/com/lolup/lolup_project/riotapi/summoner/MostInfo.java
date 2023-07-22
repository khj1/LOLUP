package com.lolup.lolup_project.riotapi.summoner;

import com.lolup.lolup_project.common.BaseTimeEntity;
import com.lolup.lolup_project.duo.domain.Duo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MostInfo extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "most_info_id")
	private Long id;

	private String name;
	private Long play;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "duo_id")
	private Duo duo;

	public MostInfo(final String name, final Long play) {
		this.name = name;
		this.play = play;
	}

	public static MostInfo create(final String name, final Long play) {
		return new MostInfo(name, play);
	}

	public void changeDuo(Duo duo) {
		this.duo = duo;
	}
}
