package com.kt.mindLog.dto.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.mindLog.domain.user.User;

public record UserProfileResponse(
	@JsonProperty("user_id")
	String userId,

	@JsonProperty("profile_image_url")
	String profileImageUrl,

	String nickname,

	@JsonProperty("nickname_change_count")
	Integer nicknameCount
) {
	public static UserProfileResponse from(final User user) {
		return new UserProfileResponse(
			user.getId().toString(),
			user.getProfileImageUrl(),
			user.getNickname(),
			user.getNicknameChangeCount()
		);
	}
}
