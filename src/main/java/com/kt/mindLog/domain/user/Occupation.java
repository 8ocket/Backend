package com.kt.mindLog.domain.user;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Occupation {
	STUDENT, JOB_SEEKER, EMPLOYEE, CAREER_SWITCHER;

	@JsonCreator
	public static Occupation from(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return Occupation.valueOf(value.toUpperCase());
	}
}
