package com.lolup.lolup_project.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/user-all")
    public void sendToAll(@Payload Message message) {
        log.info("서버로 전송된 메시지 = {}", message.getMessage());
        log.info("메시지를 보낸 유저 ID = {}", message.getMember().getId());

        Message savedMessage = messageService.save(message);

        simpMessagingTemplate.convertAndSend("/queue/user/" + message.getRoomId() , savedMessage);
    }
}