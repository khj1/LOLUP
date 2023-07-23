package com.lolup.lolup_project.message.domain;

import java.util.Optional;

import com.lolup.lolup_project.message.application.dto.MessageDto;

public interface MessageCustomRepository {
	public Optional<MessageDto> findMessageDtoById(Long id);
}
