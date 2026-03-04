package com.kt.mindLog.global.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	INVALID_FORMAT(HttpStatus.BAD_REQUEST,"잘못된 형식의 값입니다."),

	// Kakao OAuth
	KAKAO_TOKEN_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "카카오 액세스 토큰 발급에 실패했습니다."),
	KAKAO_USER_INFO_ERROR(HttpStatus.BAD_REQUEST, "카카오 유저 정보를 정상적으로 가져오지 못했습니다.")
	;

	private final HttpStatus status;
	private final String message;
}