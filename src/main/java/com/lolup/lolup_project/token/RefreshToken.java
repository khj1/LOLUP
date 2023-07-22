package com.lolup.lolup_project.token;

import com.lolup.lolup_project.base.BaseTimeEntity;
import com.lolup.lolup_project.member.domain.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "refresh_token_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	private String refreshToken;

	public RefreshToken(Member member, String refreshToken) {
		this.member = member;
		this.refreshToken = refreshToken;
	}

	public static RefreshToken create(Member member, String refreshToken) {
		return new RefreshToken(member, refreshToken);
	}
}
