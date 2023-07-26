package com.lolup.member.domain;

import java.util.List;

import com.lolup.common.BaseTimeEntity;
import com.lolup.message.domain.Message;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	private String name;
	private String email;
	private String picture;
	private String summonerName;

	@Enumerated(EnumType.STRING)
	private Role role;

	@OneToMany(mappedBy = "member")
	private List<Message> messages;

	public Member(final String name, final String email, final Role role, final String picture) {
		this(null, name, email, role, picture, null);
	}

	@Builder
	public Member(final Long id, final String name, final String email, final Role role, final String picture,
				  final String summonerName) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.picture = picture;
		this.summonerName = summonerName;
		this.role = role;
	}

	public void update(final String name, final String email, final String picture) {
		this.name = name;
		this.email = email;
		this.picture = picture;
	}

	public String getRoleKey() {
		return role.getKey();
	}

	public void changeSummonerName(final String summonerName) {
		this.summonerName = summonerName;
	}
}
