package com.lolup.lolup_project.chat.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {

    private Long messageId;
    private Long memberId;
    private Long roomId;
    private String message;
    private LocalDateTime date;

    @Builder
    public ChatMessage(Long messageId, Long memberId, Long roomId, String message, LocalDateTime date) {
        this.messageId = messageId;
        this.memberId = memberId;
        this.roomId = roomId;
        this.message = message;
        this.date = date;
    }
}
