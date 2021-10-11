package com.lolup.lolup_project.message;

import com.lolup.lolup_project.base.BaseTimeEntity;
import com.lolup.lolup_project.member.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Long roomId;
    private String message;

    private void changeMember(Member member) {
        this.member = member;
        member.getMessages().add(this);
    }

    public Message(Member member, Long roomId, String message) {
        changeMember(member);
        this.roomId = roomId;
        this.message = message;
    }

    public static Message create(Member member, Long roomId, String message) {
        return new Message(member, roomId, message);
    }
}
