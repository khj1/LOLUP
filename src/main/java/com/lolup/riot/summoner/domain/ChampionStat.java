package com.lolup.riot.summoner.domain;

import com.lolup.common.BaseTimeEntity;
import com.lolup.duo.domain.Duo;

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
public class ChampionStat extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "most_info_id")
	private Long id;

	private String name;
	private Long count;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "duo_id")
	private Duo duo;

	public ChampionStat(final String name, final Long count) {
		this.name = name;
		this.count = count;
	}

	public static ChampionStat create(final String name, final Long count) {
		return new ChampionStat(name, count);
	}

	public void changeDuo(final Duo duo) {
		this.duo = duo;
	}
}
