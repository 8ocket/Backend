package com.kt.mindLog.dto.user.response;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserProfileResponse(
	@JsonProperty("user_id")
	String userId,

	@JsonProperty("profile_image_url")
	String profileImageUrl,

	String nickname,

	@JsonProperty("updated_at")
	String updatedAt
) {
	public static UserProfileResponse updateProfile(final UUID userId, final String profileImageUrl, final String nickname) {
		return new UserProfileResponse(
			userId.toString(),
			profileImageUrl,
			nickname,
			LocalDateTime.now().toString()
		);
	}
}
