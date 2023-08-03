package com.lolup.riot.match.application.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchDto {

	private MatchInfoDto info;

	public List<ParticipantDto> getParticipants() {
		return info.getParticipants();
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class MatchInfoDto {

		private List<ParticipantDto> participants;
	}
}
