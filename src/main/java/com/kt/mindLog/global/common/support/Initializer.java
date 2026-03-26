package com.kt.mindLog.global.common.support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kt.mindLog.domain.persona.Persona;
import com.kt.mindLog.repository.PersonaRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Initializer {
	private final PersonaRepository personaRepository;

	@Value("${TEST_PERSONA_IMAGE_URL}")
	private String personaImageUrl;

	@PostConstruct
	@Transactional
	public void init() {
		if (personaRepository.count() > 0) {
			return;
		}

		var persona = Persona.builder()
			.personaType("mental_health")
			.personaName("나무")
			.description("감정 소진, 불안, 우울감 등 정서적 케어와 공감에 집중합니다.")
			.toneSettings("""
				{
					 "persona_name": "나무",
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

		persona.updatePersonaImageUrl(personaImageUrl);

		personaRepository.save(persona);
	}
}
