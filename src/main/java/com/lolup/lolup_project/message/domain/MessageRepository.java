package com.lolup.lolup_project.message.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long>, MessageCustomRepository {
}
