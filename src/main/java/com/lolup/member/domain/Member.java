package com.lolup.member.domain;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lolup.common.BaseTimeEntity;
import com.lolup.member.exception.InvalidEmailException;
import com.lolup.member.exception.InvalidMemberNameException;
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

	public static final int MIN_NAME_LENGTH = 1;
	public static final int MAX_NAME_LENGTH = 16;

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$");

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String picture;

	private String summonerName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@OneToMany(mappedBy = "member")
	private List<Message> messages;

	public Member(final String name, final String email, final Role role, final String picture) {
		this(name, email, role, picture, null);
	}

	@Builder
	public Member(final String name, final String email, final Role role, final String picture,
				  final String summonerName) {
		validateMember(name, email);

		this.name = name;
		this.email = email;
		this.picture = picture;
		this.summonerName = summonerName;
		this.role = role;
	}

	private void validateMember(final String name, final String email) {
		validateName(name);
		validateEmail(email);
	}

	private void validateName(final String name) {
		if (name.isBlank() || MAX_NAME_LENGTH < name.length()) {
			throw new InvalidMemberNameException();
		}
	}

	private void validateEmail(final String email) {
		Matcher matcher = EMAIL_PATTERN.matcher(email);
		if (!matcher.matches()) {
			throw new InvalidEmailException();
		}
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
