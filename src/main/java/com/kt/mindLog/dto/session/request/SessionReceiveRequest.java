package com.kt.mindLog.dto.session.request;

import jakarta.validation.constraints.NotBlank;

public record SessionReceiveRequest(
	@NotBlank(message = "상담 메세지는 필수 입력 사항입니다")
	String content
) {
}
