package com.kt.mindLog.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	INVALID_FORMAT(HttpStatus.BAD_REQUEST,"잘못된 형식의 값입니다.");

	private final HttpStatus status;
	private final String message;
}