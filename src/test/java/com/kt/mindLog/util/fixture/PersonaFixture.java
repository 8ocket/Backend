package com.kt.mindLog.util.fixture;

import java.util.UUID;

import org.springframework.test.util.ReflectionTestUtils;

import com.kt.mindLog.domain.persona.Persona;

public abstract class PersonaFixture {

	private static final String DEFAULT_PERSONA_URL = "https://s3.example.com/default-persona.png";

	public static Persona initPersona() {
		return buildPersona();
	}

	protected static Persona buildPersona() {
		var persona = Persona.builder()
			.personaType("mental_health")
			.personaName("나봄이")
			.description("감정 소진, 불안, 우울감 등 정서적 케어와 공감에 집중합니다.")
			.toneSettings("""
				{
					 "persona_name": "나봄이",
					 "style":    "따뜻하고 공감적인",
					 "focus":    "정서적 공감과 감정 탐색",
					 "traits":   "판단하지 않고 경청하며, 감정을 먼저 수용한다",
					 "approach": "감정 반영 → 정서 탐색 → 안정화 제안",
					 "language": "부드러운 존댓말, 감정 언어 적극 사용",
				}
				""")
			.unlockCredits(0)
			.isDefault(true)
			.build();

		persona.updatePersonaImageUrl(DEFAULT_PERSONA_URL);

		ReflectionTestUtils.setField(persona, "id", UUID.randomUUID());

		return persona;
	}
}
