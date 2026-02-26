package com.kt.mindLog.dto.user.response;

import lombok.Builder;

@Builder
public record LoginResponse(
	String accessToken,
	String refreshToken,
	boolean isNewUser
) {
	public static LoginResponse of(String accessToken, String refreshToken, boolean isNewUser) {
		return LoginResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.isNewUser(isNewUser)
			.build();
	}
}