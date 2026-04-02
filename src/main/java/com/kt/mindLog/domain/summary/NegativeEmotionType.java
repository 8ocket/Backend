package com.kt.mindLog.domain.summary;

import java.util.Arrays;

public enum NegativeEmotionType {
	APPREHENSION, FEAR, TERROR,
	PENSIVENESS, SADNESS, GRIEF,
	BOREDOM, DISGUST, LOATHING,
	ANNOYANCE, ANGER, RAGE;

	public static boolean contains(EmotionType type) {
		return Arrays.stream(values())
			.anyMatch(e -> e.name().equals(type.name()));
	}
}
