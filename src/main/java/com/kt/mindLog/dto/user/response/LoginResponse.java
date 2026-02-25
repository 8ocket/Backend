package com.kt.mindLog.dto.user.response;

import lombok.Builder;

@Builder
public record LoginResponse(
	String accessToken,
	String refreshToken
) {
	public static LoginResponse of(String accessToken, String refreshToken) {
		return LoginResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}
}