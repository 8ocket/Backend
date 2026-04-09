package com.kt.mindLog.domain.user;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Gender {
	MALE, FEMALE;

	@JsonCreator
	public static Gender from(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return Gender.valueOf(value.toUpperCase());
	}
}
