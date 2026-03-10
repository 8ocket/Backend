package com.kt.mindLog.dto.oauth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleUserInfoResponse(
	@JsonProperty("email")
	String email,

	@JsonProperty("email_verified")
	Boolean emailVerified
) {
}
