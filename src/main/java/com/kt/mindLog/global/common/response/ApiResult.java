package com.kt.mindLog.global.common.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResult<T> {
	private String code;
	private String message;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T data;
	private LocalDateTime timestamp;

	public static ApiResult<Void> ok() {
		return ApiResult.of("ok", "성공", null, LocalDateTime.now());
	}

	public static <T> ApiResult<T> ok(T data) {
		return ApiResult.of("ok", "성공", data, LocalDateTime.now());
	}

	public static <T> ApiResult<T> error(String code, String message) {
		return ApiResult.of(code, message, null, LocalDateTime.now());
	}

	public static <T> ApiResult<T> of(String message, T data) {
		return ApiResult.of("ok", message, data, LocalDateTime.now());
	}

	private static <T> ApiResult<T> of(String code, String message, T data,  LocalDateTime timestamp) {
		return new ApiResult<>(code, message, data, timestamp);
	}
}