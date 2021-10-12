package com.lolup.lolup_project.message;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MessageDto {
    private Long memberId;
    private Long roomId;
    private String message;
    private LocalDateTime date;

    public MessageDto(Long memberId, Long roomId, String message, LocalDateTime date) {
        this.memberId = memberId;
        this.roomId = roomId;
        this.message = message;
        this.date = date;
    }
}
