package com.kt.mindLog.dto.session.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

public record SessionCreateRequest(
	@NotBlank(message = "상담 메세지는 필수 입력 사항입니다")
	@JsonProperty("first_content")
	String firstContent
) {
}
