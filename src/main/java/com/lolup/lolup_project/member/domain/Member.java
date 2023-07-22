package com.lolup.lolup_project.member.domain;

import java.util.List;

import com.lolup.lolup_project.common.BaseTimeEntity;
import com.lolup.lolup_project.message.Message;

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

	public Member(String name, String email, Role role, String picture) {
		this(null, name, email, role, picture, null);
	}

	@Builder
	public Member(Long id, String name, String email, Role role, String picture, String summonerName) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.picture = picture;
		this.summonerName = summonerName;
		this.role = role;
	}

	public Member update(String name, String email, String picture) {
		this.name = name;
		this.email = email;
		this.picture = picture;

		return this;
	}

	public String getRoleKey() {
		return this.role.getKey();
	}

	public void changeSummonerName(String summonerName) {
		this.summonerName = summonerName;
	}
}
