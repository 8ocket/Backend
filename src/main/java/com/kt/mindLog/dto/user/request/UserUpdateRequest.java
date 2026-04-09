package com.kt.mindLog.dto.user.request;

import org.jspecify.annotations.Nullable;

import com.kt.mindLog.domain.user.Gender;
import com.kt.mindLog.domain.user.Occupation;

public record UserUpdateRequest(
	@Nullable
	String nickname,

	@Nullable
	Occupation occupation,

	@Nullable
	Integer age,

	@Nullable
	Gender gender
) {
}
