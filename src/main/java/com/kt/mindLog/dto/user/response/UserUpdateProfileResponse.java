package com.kt.mindLog.dto.user.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserUpdateProfileResponse(
	@JsonProperty("user_id")
	String userId,

	@JsonProperty("profile_image_url")
	String profileImageUrl,

	String nickname,

	@JsonProperty("updated_at")
	String updatedAt
) {
	public static UserUpdateProfileResponse updateProfile(final UUID userId, final String profileImageUrl, final String nickname) {
		return new UserUpdateProfileResponse(
			userId.toString(),
			profileImageUrl,
			nickname,
			LocalDateTime.now().toString()
		);
	}
}
