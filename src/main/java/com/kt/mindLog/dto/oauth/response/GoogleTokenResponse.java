package com.kt.mindLog.dto.oauth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleTokenResponse(
	@JsonProperty("token_type")
	String tokenType,

	@JsonProperty("access_token")
	String accessToken,

	@JsonProperty("id_token")
	String idToken,

	@JsonProperty("expires_in")
	Integer expiresIn,

	@JsonProperty("refresh_token")
	String refreshToken,

	@JsonProperty("scope")
	String scope
) {
}
