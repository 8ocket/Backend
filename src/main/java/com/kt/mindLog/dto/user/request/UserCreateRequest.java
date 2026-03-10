package com.kt.mindLog.dto.user.request;

import com.kt.mindLog.domain.enums.Gender;
import com.kt.mindLog.domain.enums.Occupation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserCreateRequest(
	@NotBlank(message = "닉네임은 필수 입력 사항입니다")
	String nickname,
	@NotNull(message = "직업은 필수 입력 사항입니다")
	Occupation occupation,
	@NotNull(message = "나이는 필수 입력 사항입니다")
	Integer age,
	@NotNull(message = "성별은 필수 입력 사항입니다")
	Gender gender
) {
}
