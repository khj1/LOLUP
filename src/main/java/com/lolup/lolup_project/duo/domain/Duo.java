package com.lolup.lolup_project.duo.domain;

import java.util.ArrayList;
import java.util.List;

import com.lolup.lolup_project.common.BaseTimeEntity;
import com.lolup.lolup_project.member.domain.Member;
import com.lolup.lolup_project.riotapi.summoner.MostInfo;
import com.lolup.lolup_project.riotapi.summoner.SummonerDto;
import com.lolup.lolup_project.riotapi.summoner.SummonerRankInfo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Duo extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "duo_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Embedded
	private SummonerRankInfo info;

	@OneToMany(mappedBy = "duo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MostInfo> mostInfos = new ArrayList<>();

	private String position;
	private double latestWinRate;

	@Column(name = "description")
	private String desc;

	@Builder
	public Duo(final Member member, final SummonerRankInfo info, final List<MostInfo> mostInfos, final String position,
			   final double latestWinRate, final String desc) {
		this.member = member;
		this.info = info;
		addMostInfos(mostInfos);
		this.position = position;
		this.latestWinRate = latestWinRate;
		this.desc = desc;
	}

	public static Duo create(final Member member, final SummonerDto summonerDto, final String position,
							 final String desc) {
		return Duo.builder()
				.member(member)
				.info(summonerDto.getInfo())
				.mostInfos(summonerDto.getMost3())
				.position(position)
				.latestWinRate(summonerDto.getLatestWinRate())
				.desc(desc)
				.build();
	}

	private void addMostInfos(final List<MostInfo> mostInfos) {
		this.mostInfos = mostInfos;
		mostInfos.forEach(mostInfo -> mostInfo.changeDuo(this));
	}

	public void update(final String position, final String desc) {
		this.position = position;
		this.desc = desc;
	}
}
