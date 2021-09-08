package com.lolup.lolup_project.chat;

import com.lolup.lolup_project.chat.model.ChatMessage;
import com.lolup.lolup_project.chat.storage.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public ChatMessage save(ChatMessage chatMessage) {
        Long messageId = messageRepository.save(chatMessage);
        return messageRepository.findById(messageId);
    }
}
