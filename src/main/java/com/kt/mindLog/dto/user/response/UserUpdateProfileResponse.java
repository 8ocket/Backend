package com.kt.mindLog.dto.user.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.mindLog.domain.user.Gender;
import com.kt.mindLog.domain.user.Occupation;

public record UserUpdateProfileResponse(
	@JsonProperty("user_id")
	String userId,

	@JsonProperty("profile_image_url")
	String profileImageUrl,

	String nickname,

	String occupation,

	Integer age,

	String gender,

	@JsonProperty("updated_at")
	String updatedAt
) {
	public static UserUpdateProfileResponse updateProfile(final UUID userId, final String profileImageUrl,
		final String nickname, final Occupation occupation, final Integer age, final Gender gender) {
		return new UserUpdateProfileResponse(
			userId.toString(),
			profileImageUrl,
			nickname,
			occupation.toString(),
			age,
			gender.toString(),
			LocalDateTime.now().toString()
		);
	}
}
