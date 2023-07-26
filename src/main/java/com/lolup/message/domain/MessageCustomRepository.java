package com.lolup.message.domain;

import java.util.Optional;

import com.lolup.message.application.dto.MessageDto;

public interface MessageCustomRepository {
	public Optional<MessageDto> findMessageDtoById(Long id);
}
