package com.kt.mindLog.dto.oauth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfoResponse {
	@JsonProperty("id")
	private Long id;

	@JsonProperty("kakao_account")
	private KakaoAccount kakaoAccount;

	@Getter
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class KakaoAccount {
		@JsonProperty("email")
		private String email;

		@JsonProperty("is_email_valid")
		private Boolean isEmailValid;

		@JsonProperty("is_email_verified")
		private Boolean isEmailVerified;
	}
}
