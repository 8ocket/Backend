package com.kt.mindLog.domain.summary;

import java.util.Arrays;

public enum PositiveEmotionType {
	SERENITY, JOY, ECSTASY,
	ACCEPTANCE, TRUST, ADMIRATION,
	INTEREST, ANTICIPATION, VIGILANCE;

	public static boolean contains(EmotionType type) {
		return Arrays.stream(values())
			.anyMatch(e -> e.name().equals(type.name()));
	}
}
