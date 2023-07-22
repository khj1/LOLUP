package com.lolup.lolup_project.message;

import com.lolup.lolup_project.common.BaseTimeEntity;
import com.lolup.lolup_project.member.domain.Member;

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
public class Message extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "message_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	private Long roomId;
	private String message;

	public Message(Member member, Long roomId, String message) {
		changeMember(member);
		this.roomId = roomId;
		this.message = message;
	}

	public static Message create(Member member, Long roomId, String message) {
		return new Message(member, roomId, message);
	}

	private void changeMember(Member member) {
		this.member = member;
		member.getMessages().add(this);
	}
}
