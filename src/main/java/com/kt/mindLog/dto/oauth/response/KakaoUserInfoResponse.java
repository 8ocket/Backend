package com.kt.mindLog.dto.oauth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoUserInfoResponse(
	@JsonProperty("kakao_account")
	KakaoAccount kakaoAccount
) {
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record KakaoAccount(
		@JsonProperty("email")
		String email,

		@JsonProperty("is_email_valid")
		Boolean isEmailValid,

		@JsonProperty("is_email_verified")
		Boolean isEmailVerified
	) {}
}