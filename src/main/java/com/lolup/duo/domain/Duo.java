package com.lolup.duo.domain;

import java.util.ArrayList;
import java.util.List;

import com.lolup.common.BaseTimeEntity;
import com.lolup.member.domain.Member;
import com.lolup.riot.summoner.domain.ChampionStat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	private SummonerStat summonerStat;

	@OneToMany(mappedBy = "duo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ChampionStat> championStats = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SummonerPosition position;

	private int profileIconId;
	private double latestWinRate;

	@Column(name = "description")
	private String desc;

	@Builder
	public Duo(final Member member, final SummonerStat summonerStat, final List<ChampionStat> championStats,
			   final SummonerPosition position, final int profileIconId, final double latestWinRate,
			   final String desc) {
		this.member = member;
		this.summonerStat = summonerStat;
		addChampionStats(championStats);
		this.position = position;
		this.profileIconId = profileIconId;
		this.latestWinRate = latestWinRate;
		this.desc = desc;
	}

	public static Duo create(final Member member, final SummonerStat summonerStat,
							 final List<ChampionStat> championStats, final double latestWinRate,
							 final int profileIconId, final SummonerPosition position, final String desc) {
		return Duo.builder()
				.member(member)
				.summonerStat(summonerStat)
				.championStats(championStats)
				.position(position)
				.profileIconId(profileIconId)
				.latestWinRate(latestWinRate)
				.desc(desc)
				.build();
	}

	private void addChampionStats(final List<ChampionStat> championStats) {
		this.championStats = championStats;
		championStats.forEach(championStat -> championStat.changeDuo(this));
	}

	public void update(final SummonerPosition position, final String desc) {
		this.position = position;
		this.desc = desc;
	}

	public boolean isAuthor(final Long memberId) {
		return member.isSameMember(memberId);
	}
}
