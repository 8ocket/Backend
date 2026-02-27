package com.kt.mindLog.global.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// Kakao OAuth
	KAKAO_TOKEN_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "카카오 액세스 토큰 발급에 실패했습니다."),
	KAKAO_USER_INFO_ERROR(HttpStatus.BAD_REQUEST, "카카오 유저 정보를 정상적으로 가져오지 못했습니다."),

	//auth
	INVALID_TOKEN(HttpStatus.BAD_REQUEST, "잘못된 접근입니다. 다시 로그인을 시도해주시기 바랍니다."),
	EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "만료된 토큰입니다. 다시 로그인을 시도해주시기 바랍니다."),
	;

	private final HttpStatus status;
	private final String message;
}