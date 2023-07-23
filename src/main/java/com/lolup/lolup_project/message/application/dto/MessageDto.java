package com.lolup.lolup_project.message.application.dto;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageDto {
	private Long memberId;
	private Long roomId;
	private String message;
	private LocalDateTime date;

	public MessageDto(final Long memberId, final Long roomId, final String message, final LocalDateTime date) {
		this.memberId = memberId;
		this.roomId = roomId;
		this.message = message;
		this.date = date;
	}
}
