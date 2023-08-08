package com.lolup.duo.exception;

import static com.lolup.duo.application.DuoService.CREATION_LIMIT_MINUTES;

public class DuoCreationLimitException extends RuntimeException {

	public DuoCreationLimitException() {
		super(String.format("생성 제한 시간(%d분) 이내엔 듀오 모집글을 생성할 수 없습니다.", CREATION_LIMIT_MINUTES));
	}
}
