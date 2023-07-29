package com.lolup.auth.domain;

import com.lolup.common.BaseTimeEntity;
import com.lolup.member.domain.Member;

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

	@Column(unique = true, nullable = false)
	private String tokenValue;

	public RefreshToken(final Member member, final String tokenValue) {
		this.member = member;
		this.tokenValue = tokenValue;
	}

	public static RefreshToken create(final Member member, final String tokenValue) {
		return new RefreshToken(member, tokenValue);
	}
}
