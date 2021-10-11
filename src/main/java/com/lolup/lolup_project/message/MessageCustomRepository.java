package com.lolup.lolup_project.message;

import java.util.Optional;

public interface MessageCustomRepository {
    public Optional<MessageDto> findMessageDtoById(Long id);
}
