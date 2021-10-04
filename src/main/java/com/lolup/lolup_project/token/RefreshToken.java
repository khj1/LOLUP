package com.lolup.lolup_project.token;

import com.lolup.lolup_project.base.BaseTimeEntity;
import com.lolup.lolup_project.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "refresh_token_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String refreshToken;

    public RefreshToken(Member member, String refreshToken) {
        this.member = member;
        this.refreshToken = refreshToken;
    }

    public static RefreshToken create(Member member, String refreshToken) {
        return new RefreshToken(member, refreshToken);
    }
}
