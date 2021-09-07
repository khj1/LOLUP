package com.lolup.lolup_project.chat.storage;

import com.lolup.lolup_project.chat.model.ChatMessage;

public interface MessageRepository {

    Long save(ChatMessage message);

    ChatMessage findById(Long messageId);
}
