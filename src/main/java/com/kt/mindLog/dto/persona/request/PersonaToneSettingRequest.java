package com.kt.mindLog.dto.persona.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PersonaToneSettingRequest(
	@JsonProperty("persona_name")
	String personaName,
	String style,
	String focus,
	String traits,
	String approach,
	String language
) {
}
