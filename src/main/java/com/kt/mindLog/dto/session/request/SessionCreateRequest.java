package com.kt.mindLog.dto.session.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SessionCreateRequest(
	@NotNull(message = "페르소나는 필수 선택 사항입니다")
	@JsonProperty("persona_id")
	UUID personaId,

	@NotBlank(message = "상담 메세지는 필수 입력 사항입니다")
	@JsonProperty("first_content")
	String firstContent
) {
}
