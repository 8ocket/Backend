package com.kt.mindLog.global.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// KAKAO OAuth
	KAKAO_TOKEN_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "카카오 액세스 토큰 발급에 실패했습니다."),
	KAKAO_USER_INFO_ERROR(HttpStatus.BAD_REQUEST, "카카오 유저 정보를 정상적으로 가져오지 못했습니다."),

	// GOOGLE OAuth
	GOOGLE_TOKEN_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "구글 액세스 토큰 발급에 실패했습니다."),
	GOOGLE_USER_INFO_ERROR(HttpStatus.BAD_REQUEST, "구글 유저 정보를 정상적으로 가져오지 못했습니다."),

	// NAVER OAuth
	NAVER_TOKEN_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "네이버 액세스 토큰 발급에 실패했습니다."),
	NAVER_USER_INFO_ERROR(HttpStatus.BAD_REQUEST, "네이버 유저 정보를 정상적으로 가져오지 못했습니다."),

	//auth
	INVALID_JWT_TOKEN(HttpStatus.BAD_REQUEST, "잘못된 접근입니다. 다시 로그인을 시도해주시기 바랍니다."),
	EXPIRED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "만료된 토큰입니다. 다시 로그인을 시도해주시기 바랍니다."),
	INVALID_JWT_TOKEN_FORMAT(HttpStatus.UNAUTHORIZED, "잘못된 형식의 JWT 토큰입니다."),
	;

	private final HttpStatus status;
	private final String message;
}