package com.lolup.lolup_project.message.domain;

import static com.lolup.lolup_project.member.QMember.member;
import static com.lolup.lolup_project.message.QMessage.message1;

import java.util.Optional;

import com.lolup.lolup_project.message.application.dto.MessageDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageCustomRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<MessageDto> findMessageDtoById(Long id) {
		MessageDto messageDto = queryFactory
				.select(Projections.fields(MessageDto.class,
						member.id.as("memberId"),
						message1.roomId,
						message1.message,
						message1.createdDate.as("date")
				))
				.from(message1)
				.join(message1.member, member)
				.where(message1.id.eq(id))
				.fetchOne();

		return Optional.ofNullable(messageDto);
	}
}
