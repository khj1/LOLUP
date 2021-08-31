package com.lolup.lolup_project.token;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshToken {

    private Long refeshTokenId;
    private Long memberId;
    private String refreshToken;

    @Builder
    public RefreshToken(Long memberId, String refreshToken) {
        this.memberId = memberId;
        this.refreshToken = refreshToken;
    }

    public static RefreshToken create(Long memberId, String refreshToken) {
        return RefreshToken.builder()
                .memberId(memberId)
                .refreshToken(refreshToken)
                .build();
    }
}
