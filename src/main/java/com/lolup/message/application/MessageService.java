package com.lolup.message.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lolup.member.domain.Member;
import com.lolup.member.domain.MemberRepository;
import com.lolup.message.application.dto.MessageDto;
import com.lolup.message.domain.Message;
import com.lolup.message.domain.MessageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {

	private final MemberRepository memberRepository;
	private final MessageRepository messageRepository;

	public MessageDto save(MessageDto messageDto) {
		Member findMember = memberRepository.findById(messageDto.getMemberId()).orElse(null);
		Message message = Message.create(findMember, messageDto.getRoomId(), messageDto.getMessage());
		messageRepository.save(message);

		return messageRepository.findMessageDtoById(message.getId()).orElse(null);
	}
}
