package com.kt.mindLog.dto.persona.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.mindLog.domain.persona.Persona;

public record UserPersonaListResponse(
	@JsonProperty("persona_id")
	String personaId,

	@JsonProperty("persona_image_url")
	String personaImageUrl,

	@JsonProperty("persona_name")
	String personaName,

	String description
) {
	public static UserPersonaListResponse from(Persona persona) {
		return  new UserPersonaListResponse(
			persona.getId().toString(),
			persona.getPersonaImageUrl(),
			persona.getPersonaName(),
			persona.getDescription()
		);
	}
}
