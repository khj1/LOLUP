package com.lolup.lolup_project.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Message save(Message message) {
        messageRepository.save(message);
        return messageRepository.findById(message.getId()).get();
    }
}
