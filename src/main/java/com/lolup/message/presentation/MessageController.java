package com.lolup.message.presentation;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.lolup.message.application.MessageService;
import com.lolup.message.application.dto.MessageDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageController {

	private final MessageService messageService;
	private final SimpMessagingTemplate simpMessagingTemplate;

	@MessageMapping("/user-all")
	public void sendToAll(@Payload MessageDto messageDto) {
		log.info("서버로 전송된 메시지 = {}", messageDto.getMessage());
		log.info("메시지를 보낸 유저 ID = {}", messageDto.getMemberId());

		MessageDto savedMessageDto = messageService.save(messageDto);

		simpMessagingTemplate.convertAndSend("/queue/user/" + messageDto.getRoomId(), savedMessageDto);
	}
}
