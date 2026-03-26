package com.kt.mindLog.dto.persona.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.mindLog.domain.persona.Persona;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PersonaCreateRequest(

	@JsonProperty("persona_type")
	@NotBlank(message = "페르소나 타입은 필수 입력 사항입니다")
	String personaType,

	@JsonProperty("persona_name")
	@NotBlank(message = "페르소나 이름은 필수 입력 사항입니다")
	String personaName,

	@NotBlank(message = "페르소나 설명은 필수 입력 사항입니다")
	String description,

	@JsonProperty("tone_settings")
	PersonaToneSettingRequest toneSettings,

	@JsonProperty("unlock_credits")
	@NotNull(message = "페르소나 해금 크레딧은 필수 입력 사항입니다")
	Integer unlockCredits,

	@JsonProperty("is_default")
	@NotNull(message = "무료 페르소나 설정은 필수 입력 사항입니다")
	boolean isDefault
) {
}